package com.example.zzan.blocklist.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zzan.blocklist.service.BlockListService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BlockListController {


	private final BlockListService blockListService;

	@PostMapping("/api/blockList/{userId}")
	public ResponseDto addBlockList (@PathVariable("userId") Long blackListedUserId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return blockListService.addBlacklist(blackListedUserId,userDetails.getUser());
	}
}
