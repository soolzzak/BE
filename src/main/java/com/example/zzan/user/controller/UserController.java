package com.example.zzan.user.controller;

import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.user.dto.PasswordRequestDto;
import com.example.zzan.user.dto.UserLoginDto;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.service.KakaoService;
import com.example.zzan.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@Tag(name = "UserController", description = "로그인 및 회원가입 파트")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @GetMapping("/login")
    public String kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws IOException, ParseException {

        String accessToken = response.getHeader("ACCESS_KEY");
        response.setHeader("ACCESS_KEY", "Bearer " + accessToken);

        return kakaoService.kakaoLogin(code, response);
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
}