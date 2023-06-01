package com.example.zzan.report.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.report.dto.ReportRequestDto;
import com.example.zzan.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping("api/report/{userId}")
	public ResponseDto userReport(@PathVariable("userId") Long userId, @RequestBody ReportRequestDto ReportRequestDto,@AuthenticationPrincipal
		UserDetailsImpl userDetails){

		return reportService.userReport(userId,ReportRequestDto,userDetails.getUser());
	}


}
