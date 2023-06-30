package com.example.zzan.global.security.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zzan.global.exception.ExceptionEnum.EMAIL_NOT_FOUND;
import static com.example.zzan.global.exception.ExceptionEnum.REFRESH_TOKEN_NOT_FOUND;
import static com.example.zzan.global.jwt.JwtUtil.REFRESH_KEY;

@Slf4j
@Tag(name = "TokenController", description = "토큰 파트")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/getAccessToken")
    public ResponseEntity<ResponseDto<TokenDto>> getAccessTokenFromRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        // String refreshToken = null;
        //
        // if (request.getCookies() != null) {
        //     for (Cookie cookie : request.getCookies()) {
        //         if (REFRESH_KEY.equals(cookie.getName())) {
        //             refreshToken = cookie.getValue();
        //             break;
        //         }
        //     }
        // }

        // if (refreshToken.startsWith("Bearer ")) {
        //     refreshToken = refreshToken.substring("Bearer ".length());
        // }

        // if (jwtUtil.refreshTokenValidation(refreshToken)) {

            return ResponseEntity.ok().body(ResponseDto.setSuccess("Successfully reissued Access Token."));
        // }
        // throw new ApiException(REFRESH_TOKEN_NOT_FOUND);
    }
}