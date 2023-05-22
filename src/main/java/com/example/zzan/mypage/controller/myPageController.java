package com.example.zzan.mypage.controller;

import java.io.IOException;

import com.example.zzan.mypage.dto.MyPageResponseDto;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.zzan.global.security.UserDetailsImpl;

import com.example.zzan.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class myPageController {

	private final MyPageService myPageService;


	@ResponseBody
	@PutMapping(value="/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public MyPageResponseDto saveImg(@RequestParam(value="image", required=false) MultipartFile image, @RequestParam(value="username", required=false) String username,@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

		return myPageService.saveMyPage(image, username, userDetails.getUser().getEmail());
	}
}

