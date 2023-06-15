package com.example.zzan.youtube.service;

import static com.example.zzan.global.exception.ExceptionEnum.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.youtube.dto.YoutubeListDto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class YoutubeService {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Value("${youtube.api.key}")
	private String mykey;

	public ResponseDto<List<YoutubeListDto>> callVideoList(int page, int size,String videoName) {
		 // SummonerDTO result;
		List<YoutubeListDto> result= new ArrayList<>();
		String encodedVideoName;

		try {
			encodedVideoName = URLEncoder.encode(videoName,StandardCharsets.UTF_8.toString());

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + encodedVideoName + "&maxResults=3&regionCode=KR&key=" + mykey))
				.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			JsonNode rootNode = objectMapper.readTree(response.body());

			JsonNode itemsNode = rootNode.get("items");

			if (itemsNode.isArray()) {
				for (JsonNode itemNode : itemsNode) {
					YoutubeListDto dto = new YoutubeListDto();
					JsonNode idNode = itemNode.get("id");
					dto.setVideoId(idNode.get("videoId").asText());
					result.add(dto);
				}
			}

			log.info("방을 생성하였습니다.",result);
		} catch (IOException |InterruptedException|URISyntaxException e) {
			throw new ApiException(SEARCH_FAILED);
		}


		return ResponseDto.setSuccess("방을 생성하였습니다.", result);
	}
}



