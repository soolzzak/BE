package com.example.zzan.global.jwt;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.exception.ExceptionEnum;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;
import com.example.zzan.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;

import static com.example.zzan.global.exception.ExceptionEnum.EMAIL_NOT_FOUND;
import static com.example.zzan.global.jwt.JwtUtil.ACCESS_KEY;
import static com.example.zzan.global.jwt.JwtUtil.REFRESH_KEY;


@Slf4j
@Component
@RequiredArgsConstructor()
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String access_token = null;
        String refresh_token = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ACCESS_KEY)) {
                    access_token = cookie.getValue();
                } else if (cookie.getName().equals(REFRESH_KEY)) {
                    refresh_token = cookie.getValue();
                }
            }
        }

        if (access_token != null) {
            String validationError = jwtUtil.validateToken(access_token);
            if (validationError == null) {
                String userEmail = jwtUtil.getUserInfoFromToken(access_token);
                setAuthentication(userEmail);
            }
        } else if (refresh_token != null) {
            if (jwtUtil.refreshTokenValidation(refresh_token)) {
                String userEmail = jwtUtil.getUserInfoFromToken(refresh_token);
                setAuthentication(userEmail);
                User user = userRepository.findUserByEmail(userEmail).orElseThrow(
                    () -> new ApiException(EMAIL_NOT_FOUND)
                );
                String newAccessToken = jwtUtil.createToken(user, UserRole.USER, "ACCESS_KEY");

                if (newAccessToken.startsWith("Bearer ")) {
                    newAccessToken = newAccessToken.substring(7);
                }

                Cookie newAccessTokenCookie = new Cookie(ACCESS_KEY, newAccessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setDomain("honsoolzzak.com");
                int oneHour = 3600;
                newAccessTokenCookie.setMaxAge(oneHour);
                response.addCookie(newAccessTokenCookie);
            } else {
                sendErrorResponse(response, ExceptionEnum.ACCESS_TOKEN_NOT_FOUND);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


    private void sendErrorResponse(HttpServletResponse response, ExceptionEnum exceptionEnum) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exceptionEnum.getStatus());
        response.setContentType("application/json;charset=UTF-8");

        String responseBody = "{\"statusCode\": " + exceptionEnum.getStatus() + ", \"message\": \"" + exceptionEnum.getMessage() + "\"}";
        response.getWriter().write(responseBody);
    }
}