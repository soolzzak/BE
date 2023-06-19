package com.example.zzan.webRtc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeMessageDto {
	private Long from;
	private String type;
	private double data;
	private Object candidate;
	private Object sdp;
}
