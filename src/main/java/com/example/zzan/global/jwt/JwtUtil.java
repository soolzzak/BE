package com.example.zzan.global.jwt;

import com.example.zzan.global.exception.ExceptionEnum;
import com.example.zzan.global.security.UserDetailsServiceImpl;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.global.security.entity.RefreshToken;
import com.example.zzan.global.security.repository.RefreshTokenRepository;
import com.example.zzan.user.entity.Gender;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.refresh.key}")
    private String refreshSecretKey;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_KEY = "ACCESS_KEY";
    public static final String REFRESH_KEY = "REFRESH_KEY";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TIME = 60 * 60 * 1000L;
    private static final long REFRESH_TIME = 7 * 24 * 60 * 60 * 1000L;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private Key key;
    private Key kakaoKey;
    private Key refreshKey;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
        kakaoKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        byte[] refreshKeyBytes = Base64.getDecoder().decode(refreshSecretKey);
        refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public TokenDto createAllToken(User user, UserRole role) {
        return new TokenDto(createToken(user, role, ACCESS_KEY), createToken(user, role, REFRESH_KEY));
    }

    public String resolveToken(HttpServletRequest request, String token) {
        String tokenName = token.equals(ACCESS_KEY) ? ACCESS_KEY : REFRESH_KEY;
        String bearerToken = request.getHeader(tokenName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String createToken(User user, UserRole role, String type) {
        Date date = new Date();
        long time = type.equals(ACCESS_KEY) ? ACCESS_TIME : REFRESH_TIME;
        String key = type.equals(ACCESS_KEY) ? secretKey : refreshSecretKey;
        Map<String, Object> claim = new HashMap<>();
        claim.put("id", user.getId());
        claim.put("role", role);
        claim.put("email", user.getEmail());
        claim.put("gender", user.getGender());
        return BEARER_PREFIX
                + Jwts.builder()
                .setSubject(user.getEmail())
                .signWith(signatureAlgorithm, key)
                .claim(AUTHORIZATION_KEY, claim)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + time))
                .compact();
    }

    public String createToken(String username, Long kakaoId, String kakaoImage, String email, Gender gender, String ageRange, String birthday) {
        Date date = new Date();
        Date exprTime = (Date) Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        return BEARER_PREFIX +
                Jwts.builder()
                        .signWith(SignatureAlgorithm.HS512, kakaoKey)
                        .claim("ACCESS_KEY", "USER")
                        .setSubject(kakaoId.toString())
                        .claim("username", username)
                        .claim("kakaoImage", kakaoImage)
                        .claim("email", email)
                        .claim("gender", gender)
                        .claim("ageRange", ageRange)
                        .claim("birthday", birthday)
                        .setExpiration(exprTime)
                        .setIssuedAt(date)
                        .signWith(kakaoKey, signatureAlgorithm)
                        .compact();
    }

    public String validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return null;
        } catch (SecurityException | MalformedJwtException e) {
            return ExceptionEnum.INVALID_JWT_SIGNATURE.getMessage();
        } catch (ExpiredJwtException e) {
            return ExceptionEnum.ACCESS_TOKEN_NOT_FOUND.getMessage();
        } catch (UnsupportedJwtException e) {
            return ExceptionEnum.UNSUPPORTED_JWT_TOKEN.getMessage();
        } catch (IllegalArgumentException e) {
            return ExceptionEnum.EMPTY_JWT_CLAIMS.getMessage();
        }
    }

    public String validateRefreshtoken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return null;
        } catch (SecurityException | MalformedJwtException e) {
            return ExceptionEnum.INVALID_JWT_SIGNATURE.getMessage();
        } catch (ExpiredJwtException e) {
            return ExceptionEnum.ACCESS_TOKEN_NOT_FOUND.getMessage();
        } catch (UnsupportedJwtException e) {
            return ExceptionEnum.UNSUPPORTED_JWT_TOKEN.getMessage();
        } catch (IllegalArgumentException e) {
            return ExceptionEnum.EMPTY_JWT_CLAIMS.getMessage();
        }
    }

    public String getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getUserInfoFromRefreshtoken(String token) {
        return Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication createAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Boolean refreshTokenValidation(String token) {
        String validationError = validateRefreshtoken(token);
        log.info("Adding user {} to blacklist for user {}", validationError, token);
        if (validationError != null) {
            return false;
        }
        String userEmail = getUserInfoFromRefreshtoken(token);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(userEmail);
        String actualRefreshToken = refreshToken.get().getRefreshToken();
        return refreshToken.isPresent() && token.equals(actualRefreshToken);
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_KEY, accessToken);
    }

    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(REFRESH_KEY, refreshToken);
    }
}