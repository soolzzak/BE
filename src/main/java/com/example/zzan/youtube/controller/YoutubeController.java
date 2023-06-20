package com.example.zzan.youtube.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.youtube.dto.YoutubeListDto;
import com.example.zzan.youtube.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class YoutubeController {

	private final YoutubeService youtubeService;

	@GetMapping(value = "/youtubeSearch")
	public ResponseDto<List<YoutubeListDto>> callVideoList(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam("youtubeSearch") String youtubeSearch){
		return youtubeService.callVideoList(page,size,youtubeSearch);
	}
}
