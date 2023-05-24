package com.example.zzan.mypage.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.zzan.mypage.dto.MypageChangeDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class myPageController {

	private final MyPageService myPageService;


	@ResponseBody
	@PutMapping(value="/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<MypageChangeDto> saveImg(@RequestParam(value="image", required=false) MultipartFile image, @RequestParam(value="username", required=false) String username,@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

		return myPageService.saveMyPage(image, username, userDetails.getUser().getEmail());
	}

	@GetMapping("/mypage")
	public ResponseDto<MyPageResponseDto> getUserInfo (@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return myPageService.getUserInfo(userDetails.getUser());
	}



}

