package com.example.zzan.blacklist.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.zzan.blacklist.service.BlacklistService;
import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BlacklistController {


	private final BlacklistService blacklistService;

	@PostMapping("/api/blacklist/{userId}")
	public ResponseDto addBlacklist(@PathVariable("userId") Long blackListedUserId,@AuthenticationPrincipal UserDetailsImpl userDetails){

		return blacklistService.addBlacklist(blackListedUserId,userDetails.getUser());
	}
}
