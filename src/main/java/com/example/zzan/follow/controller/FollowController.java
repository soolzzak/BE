package com.example.zzan.follow.controller;

import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.dto.FollowRuquestDto;
import com.example.zzan.follow.service.FollowService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	@PostMapping("/follow")
	public ResponseDto<FollowResponseDto> getFollow(@RequestBody FollowRuquestDto followRuquestDto,  @AuthenticationPrincipal UserDetailsImpl userDetails){

		return followService.getFollow(followRuquestDto,userDetails.getUser());

	}

}
