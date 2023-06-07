package com.example.zzan.report.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.report.dto.ReportRequestDto;
import com.example.zzan.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@CrossOrigin
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
