package com.example.zzan.blocklist.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.zzan.blocklist.service.BlockListService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlockListController {

	private final BlockListService blockListService;

	@PutMapping ("/blockList/{userId}")
	public ResponseDto updateBlock (@PathVariable("userId") Long blockListedUserId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return blockListService.updateBlock(blockListedUserId,userDetails.getUser());
	}
}
