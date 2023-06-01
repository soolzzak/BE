package com.example.zzan.report.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReportRequestDto {

	private String reportKind;

	@Nullable
	private String another;

	// public ReportRequestDto(String reportKind,String another){
	//
	// 	this.reportKind = reportKind;
	// 	this.another = another;
	// }

}
