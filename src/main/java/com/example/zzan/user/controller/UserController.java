package com.example.zzan.user.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.user.dto.DeleteAccountRequestDto;
import com.example.zzan.user.dto.PasswordRequestDto;
import com.example.zzan.user.dto.UserLoginDto;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.service.KakaoService;
import com.example.zzan.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.zzan.global.jwt.JwtUtil.ACCESS_KEY;
import static com.example.zzan.global.jwt.JwtUtil.REFRESH_KEY;

@Tag(name = "UserController", description = "로그인 및 회원가입 파트")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @GetMapping("/signup")
    public ResponseDto checkUsername(@RequestParam("username") String username) {
        return userService.checkUsername(username);
    }

    @GetMapping("/login")
    public ResponseEntity<ResponseDto<TokenDto>> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws JsonProcessingException {
        String tokenJson = kakaoService.kakaoLogin(code, response);

        ObjectMapper objectMapper = new ObjectMapper();
        TokenDto tokenDto = objectMapper.readValue(tokenJson, TokenDto.class);

        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        Cookie accessTokenCookie = new Cookie(ACCESS_KEY, accessToken);
        String domain = "honsoolzzak.com";
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setDomain(domain);
        response.addCookie(accessTokenCookie);

        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        Cookie refreshTokenCookie = new Cookie(REFRESH_KEY, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setDomain(domain);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().body(ResponseDto.setSuccess("Token has been issued."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequestDto passwordRequestDto) {
        return userService.changePassword(passwordRequestDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.logout(userDetails.getUser());
    }

    @PostMapping("/deleteAccount")
    public ResponseDto deleteAccount(@Valid @RequestBody DeleteAccountRequestDto deleteAccountRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteAccount(deleteAccountRequestDto, userDetails.getUser());
    }

    @GetMapping("/kakaoDeleteAccount")
    public ResponseDto<RoomResponseDto> kakaoDeleteAccount(@RequestParam("code") String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoDeleteAccount(code, response);
    }

    @GetMapping("/userinfo")
    public ResponseDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserInfo(userDetails.getUser());
    }
}