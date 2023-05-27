package com.example.zzan.global.util;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.security.UserDetailsServiceImpl;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.global.security.entity.RefreshToken;
import com.example.zzan.global.security.repository.RefreshTokenRepository;
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
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static com.example.zzan.global.exception.ExceptionEnum.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_KEY = "ACCESS_KEY";
    public static final String REFRESH_KEY = "REFRESH_KEY";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TIME = 60 * 60 * 1000L;
    private static final long REFRESH_TIME = 24 * 60 * 60 * 1000L;

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenDto createAllToken(String userId, UserRole role) {
        return new TokenDto(createToken(userId, role, "Access"), createToken(userId, role, "Refresh"));
    }

    public String resolveToken(HttpServletRequest request, String token) {
        String tokenName = token.equals(ACCESS_KEY) ? ACCESS_KEY : REFRESH_KEY;
        String bearerToken = request.getHeader(tokenName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String createToken(String kakaoId) {
        Date date = new Date();
        Date exprTime = (Date) Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        return BEARER_PREFIX +
                Jwts.builder()
                        .signWith(SignatureAlgorithm.HS512, AUTHORIZATION_KEY)
                        .claim(AUTHORIZATION_HEADER, "USER")
                        .setSubject(kakaoId)
                        .setExpiration(exprTime)
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String createToken(String userId, UserRole role, String type) {
        Date date = new Date();
        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return BEARER_PREFIX
                + Jwts.builder()
                .setSubject(userId)
                .signWith(signatureAlgorithm, secretKey)
                .claim(AUTHORIZATION_KEY, role)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + time))
                .compact();
    }

    public boolean validateToken(String token) throws SecurityException, MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException {
        boolean isValidToken = false;

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            isValidToken = true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new ApiException(INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new ApiException(ACCESS_TOKEN_NOT_FOUND);
        } catch (UnsupportedJwtException e) {
            throw new ApiException(UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new ApiException(EMPTY_JWT_CLAIMS);
        }
        return isValidToken;
    }

    public String getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication createAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Boolean refreshTokenValidation(String token) {
        if (!validateToken(token)) return false;
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(getUserInfoFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_KEY, accessToken);
    }
}
