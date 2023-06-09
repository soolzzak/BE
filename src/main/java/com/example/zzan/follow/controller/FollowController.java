package com.example.zzan.follow.controller;

import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.service.FollowService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FollowController", description = "팔로우 파트")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {

	private final FollowService followService;

	@PutMapping("/follow/{followId}")
	public ResponseDto<FollowResponseDto> toggleFollow(@PathVariable Long followId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		User user = userDetails.getUser();
		return followService.updateFollow( followId, user);
	}
}
