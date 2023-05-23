//package com.example.zzan.user.controller;
//
//import com.example.zzan.global.dto.ResponseDto;
//import com.example.zzan.user.service.KakaoService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//public class KakaoController {
//    private final KakaoService kakaoService;
//
//    @PostMapping("304ee0e55e479a434eefe141a682a504")
//    public ResponseDto<String> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws JsonProcessingException {
//        return kakaoService.kakaoLogin(code, response);
//    }
//}
