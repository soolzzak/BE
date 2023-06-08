package com.example.zzan.mypage.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.dto.MypageChangeDto;
import com.example.zzan.mypage.dto.RelatedUserResponseDto;
import com.example.zzan.mypage.service.MyPageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "MyPageController", description = "마이페이지 파트")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MyPageController {

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

	@GetMapping("/mypage/{targetId}")
	public ResponseDto<RelatedUserResponseDto> UserInfoFromId (@PathVariable Long targetId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return myPageService.UserInfoFromId(targetId,userDetails.getUser());
	}
}

