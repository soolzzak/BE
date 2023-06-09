package com.example.zzan.report.controller;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.security.UserDetailsImpl;
import com.example.zzan.report.dto.ReportRequestDto;
import com.example.zzan.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ReportController", description = "신고 파트")
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
