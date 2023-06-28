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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseDto<TokenDto>> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response)  throws JsonProcessingException {

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

    @PostMapping("/deleteAccount")
    public ResponseDto deleteAccount(@Valid @RequestBody DeleteAccountRequestDto deleteAccountRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteAccount(deleteAccountRequestDto,userDetails.getUser());
    }

    @GetMapping("/kakaoDeleteAccount")
    public ResponseDto<RoomResponseDto> kakaoDeleteAccount(@RequestParam("code") String code,HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoDeleteAccount(code,response);
    }

}
