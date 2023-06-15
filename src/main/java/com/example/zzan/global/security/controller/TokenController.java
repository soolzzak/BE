package com.example.zzan.global.security.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zzan.global.exception.ExceptionEnum.*;
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
    public ResponseEntity<ResponseDto<TokenDto>> getAccessTokenFromRefreshToken(@RequestHeader(REFRESH_KEY) String refreshToken) {

        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring("Bearer ".length());
        }

        if (jwtUtil.refreshTokenValidation(refreshToken)) {
            String userEmail = jwtUtil.getUserInfoFromToken(refreshToken);
            User user = userRepository.findUserByEmail(userEmail).orElseThrow(
                    () -> new ApiException(EMAIL_NOT_FOUND)
            );
            return ResponseEntity.ok().body(ResponseDto.setSuccess("Successfully reissued Access Token."));
        }
        throw new ApiException(REFRESH_TOKEN_NOT_FOUND);
    }
}