package com.example.zzan.follow.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.dto.FollowRuquestDto;
import com.example.zzan.follow.service.FollowService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	@PostMapping("/follow/{followId}")
	public ResponseDto<FollowResponseDto> getFollow(@PathVariable("followId") Long followId,  @AuthenticationPrincipal UserDetailsImpl userDetails){

		return followService.getFollow(followId,userDetails.getUser());

	}

	@DeleteMapping("/follow/{deleteId}")
	public ResponseDto<FollowResponseDto> deleteFollow(@PathVariable("deleteId") Long followId,  @AuthenticationPrincipal UserDetailsImpl userDetails){
		return followService.deleteFollow(followId,userDetails.getUser());
	}

}
