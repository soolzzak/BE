package com.example.zzan.mypage.controller;

import java.io.IOException;
import java.util.List;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.mypage.dto.MyPageResponseDto;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.zzan.global.security.UserDetailsImpl;

import com.example.zzan.mypage.service.MyPageService;
import com.example.zzan.roomreport.dto.UserReportDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class myPageController {

	private final MyPageService myPageService;


	@ResponseBody
	@PutMapping(value="/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<MyPageResponseDto> saveImg(@RequestParam(value="image", required=false) MultipartFile image, @RequestParam(value="username", required=false) String username,@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

		return myPageService.saveMyPage(image, username, userDetails.getUser().getEmail());
	}

	@GetMapping("/mypage/meetuser")
	public ResponseDto<List<UserReportDto>> getMeetUser (@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return myPageService.getMeetUser(userDetails.getUser());
	}




}

