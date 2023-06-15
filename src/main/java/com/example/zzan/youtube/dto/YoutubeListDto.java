package com.example.zzan.youtube.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YoutubeListDto {
	private String videoId;

	public YoutubeListDto() {
	}

	public YoutubeListDto(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
}
