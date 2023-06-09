package com.example.zzan.global.security.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.global.util.JwtUtil;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;
import com.example.zzan.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zzan.global.exception.ExceptionEnum.EMAIL_NOT_FOUND;
import static com.example.zzan.global.util.JwtUtil.REFRESH_KEY;


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
            String newAccessToken = jwtUtil.createToken(user, UserRole.USER, JwtUtil.ACCESS_KEY);

            log.info("Adding user {} to blacklist for user {}", newAccessToken);
            HttpHeaders headers = new HttpHeaders();
            headers.add(JwtUtil.ACCESS_KEY, newAccessToken);

            TokenDto tokenDto = new TokenDto(newAccessToken, refreshToken);

        log.info("Adding user {} to blacklist for user {}",newAccessToken);
            return ResponseEntity.ok().headers(headers).body(ResponseDto.setSuccess("Access Token 재발행 하였습니다.", tokenDto));
         }
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDto.setBadRequest("Refresh Token 값이 만료되었습니다."));
    }
}