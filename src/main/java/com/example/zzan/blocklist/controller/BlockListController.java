package com.example.zzan.blocklist.controller;

import com.example.zzan.blocklist.service.BlockListService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
