package com.example.zzan.mypage.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.mypage.dto.MyPageResponseDto;
import com.example.zzan.mypage.dto.MypageChangeDto;
import com.example.zzan.mypage.dto.RelatedUserResponseDto;
import com.example.zzan.mypage.dto.UserSearchDto;
import com.example.zzan.mypage.service.MyPageService;
import com.example.zzan.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Tag(name = "MyPageController", description = "마이페이지 파트")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

	private final MyPageService myPageService;

	@ResponseBody
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<MypageChangeDto> saveImg(@RequestParam(value="userImage", required=false) MultipartFile userImage, @RequestParam(value="username", required=false) String username,@AuthenticationPrincipal UserDetailsImpl userDetails,  @RequestParam(value="introduction", required=false) String introduction) throws IOException {
		return myPageService.saveMyPage(userImage, username, userDetails.getUser().getEmail(), introduction);
	}

	@GetMapping
	public ResponseDto<MyPageResponseDto> getUserInfo (@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return myPageService.getUserInfo(userDetails.getUser());
	}

	@GetMapping("/{targetId}")
	public ResponseDto<RelatedUserResponseDto> UserInfoFromId (@PathVariable Long targetId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return myPageService.UserInfoFromId(targetId,userDetails.getUser());
	}
	@GetMapping("/search")
	public ResponseEntity<ResponseDto<List<UserSearchDto>>> searchUsersByUsername(@RequestParam("username") String username, Principal principal) {
		try {
			User currentUser = myPageService.findUser(principal.getName());
			ResponseDto<List<UserSearchDto>> response = myPageService.searchUserByUsername(username, currentUser);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			if (e instanceof ApiException) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.setBadRequest(e.getMessage()));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.setBadRequest("Internal server error."));
			}
		}
	}
}

