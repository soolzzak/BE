package com.example.zzan.user.controller;


import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.util.JwtUtil;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.dto.UserLoginDto;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.service.KakaoService;
import com.example.zzan.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    @GetMapping("/login/oauth2/code/kakao")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        String createToken = kakaoService.kakaoLogin(code, response);

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return "index";
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDto requestDto){
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    @GetMapping("/logout/{userEmail}")
    public ResponseEntity<?> logout(@PathVariable String userEmail) {
        return userService.logout(userEmail);
    }
}
