package com.example.zzan.user.controller;


import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.util.JwtUtil;
import com.example.zzan.user.dto.UserRequestDto;
import com.example.zzan.user.dto.UserLoginDto;
import com.example.zzan.user.service.KakaoService;
import com.example.zzan.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
@Slf4j
@RestController
@RequiredArgsConstructor
//@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;


    @GetMapping("/user")
    public String kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        String createToken = kakaoService.kakaoLogin(code, response);

        // Create the cookie
        Cookie cookie = new Cookie("access_token", createToken.substring(7)
        );
        cookie.setPath("/");
        cookie.setMaxAge(3600); // Set the desired expiration time for the cookie (in seconds)

        // Add the cookie to the response
        response.addCookie(cookie);
        return "index";
    }
    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody UserRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for(FieldError fieldError: bindingResult.getFieldErrors()) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserLoginDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    @GetMapping("/logout/{userEmail}")
    public ResponseEntity logout(@PathVariable String userEmail) {
       return userService.logout(userEmail);
    }
}
