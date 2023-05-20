package com.example.zzan.global.jwt;

import com.example.zzan.user.entity.UserRole;
import com.example.zzan.user.repository.UserRepository;
import com.example.zzan.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.zzan.global.util.JwtUtil.ACCESS_KEY;
import static com.example.zzan.global.util.JwtUtil.REFRESH_KEY;

@Slf4j
@Component
@RequiredArgsConstructor()
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

//    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
//        this.jwtUtil = jwtUtil;
//        this.userRepository = userRepository;
//    }



    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain)throws ServletException, IOException
    {
        String access_token = jwtUtil.resolveToken(request, ACCESS_KEY);
        String refresh_token = jwtUtil.resolveToken(request, REFRESH_KEY);

        if (access_token != null && jwtUtil.validateToken(access_token)) {
            String userEmail = jwtUtil.getUserInfoFromToken(access_token);
            setAuthentication(userEmail);
        } else if (refresh_token != null && jwtUtil.refreshTokenValidation(refresh_token)) {
            String userEmail = jwtUtil.getUserInfoFromToken(refresh_token);
            setAuthentication(userEmail);
            String newAccessToken = jwtUtil.createToken(userEmail, UserRole.USER, "Access");
            jwtUtil.setHeaderAccessToken(response, newAccessToken);
        }

        filterChain.doFilter(request, response);
    }
}
