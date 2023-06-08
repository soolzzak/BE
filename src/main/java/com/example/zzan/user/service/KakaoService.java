package com.example.zzan.user.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.util.JwtUtil;
import com.example.zzan.user.dto.KakaoInfoDto;
import com.example.zzan.user.entity.Gender;
import com.example.zzan.user.entity.KakaoUser;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.repository.KakaoUserRepository;
import com.example.zzan.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final KakaoUserRepository kakaoUserRepository;
    private final JwtUtil jwtUtil;

    public String kakaoLogin(String code, HttpServletResponse response) throws IOException {
        String accessToken = getToken(code);

        KakaoInfoDto kakaoInfoDto = getUserInfo(accessToken);

        if (!kakaoUserRepository.existsByKakaoId(kakaoInfoDto.getKakaoId().toString())) {
            kakaoUserRepository.save(new KakaoUser(kakaoInfoDto));
        }

        String token = jwtUtil.createToken(
                kakaoInfoDto.getUsername(),
                kakaoInfoDto.getKakaoId(),
                kakaoInfoDto.getKakaoImage(),
                kakaoInfoDto.getEmail(),
                kakaoInfoDto.getGender(),
                kakaoInfoDto.getAgeRange(),
                kakaoInfoDto.getBirthday()
        );

        response.addHeader("ACCESS_KEY", token);

        Cookie cookie = new Cookie("AccessToken", token.substring(7));
        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setPath("/");
        response.addCookie(cookie);

        // String redirectUrl = "https://honsoolzzak.com";
        // response.sendRedirect(redirectUrl);

        return "redirect:https://honsoolzzak.com";
    }

    private String getToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoApiKey);
//        body.add("redirect_uri", "http://localhost:8080/api/login");
        body.add("redirect_uri", "https://api.honsoolzzak.com/api/login");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST, kakaoTokenRequest, String.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 토큰에서 사용자 정보 get
    private KakaoInfoDto getUserInfo(String token) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> UserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST, UserInfoRequest, String.class);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String username = jsonNode.get("properties").get("nickname").asText();
        Long kakaoId = jsonNode.get("id").asLong();
        String kakaoImage = jsonNode.get("properties").get("profile_image").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();
        Gender gender = Gender.valueOf(jsonNode.get("kakao_account").get("gender").asText().toUpperCase());
        String ageRange = jsonNode.get("kakao_account").get("age_range").asText();
        String birthday = jsonNode.get("kakao_account").get("birthday").asText();

        KakaoInfoDto kakaoInfoDto = new KakaoInfoDto(username, kakaoId, kakaoImage, email, gender, ageRange, birthday);
        kakaoInfoDto.setKakaoImage(kakaoImage);
        kakaoInfoDto.setEmail(email);
        kakaoInfoDto.setGender(gender);
        kakaoInfoDto.setAgeRange(ageRange);
        kakaoInfoDto.setBirthday(birthday);

        return kakaoInfoDto;
    }
}

