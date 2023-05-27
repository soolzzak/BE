package com.example.zzan.mypage.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.dto.MypageChangeDto;
import com.example.zzan.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class myPageController {

	private final MyPageService myPageService;


	@ResponseBody
	@PutMapping(value="/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<MypageChangeDto> saveImg(@RequestParam(value="userImage", required=false) MultipartFile userImage, @RequestParam(value="username", required=false) String username,@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

		return myPageService.saveMyPage(userImage, username, userDetails.getUser().getEmail());
	}

	@GetMapping("/mypage")
	public ResponseDto<MyPageResponseDto> getUserInfo (@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return myPageService.getUserInfo(userDetails.getUser());
	}
}

