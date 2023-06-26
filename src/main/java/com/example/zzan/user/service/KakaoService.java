package com.example.zzan.user.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.jwt.JwtUtil;
import com.example.zzan.global.security.entity.RefreshToken;
import com.example.zzan.global.security.repository.RefreshTokenRepository;
import com.example.zzan.global.util.S3Uploader;
import com.example.zzan.redis.Service.RedisTokenService;
import com.example.zzan.user.dto.KakaoInfoDto;
import com.example.zzan.user.entity.Gender;
import com.example.zzan.user.entity.KakaoUser;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;
import com.example.zzan.user.repository.KakaoUserRepository;
import com.example.zzan.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.zzan.global.exception.ExceptionEnum.*;
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    private final KakaoUserRepository kakaoUserRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Uploader s3Uploader;
    private final RedisTokenService redisTokenService;

    public String kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getToken(code);
        KakaoInfoDto kakaoInfoDto = getUserInfo(accessToken);
        String ageRange = kakaoInfoDto.getAgeRange();
        String[] ages = ageRange.split("~");
        int lowerAge = Integer.parseInt(ages[0]);

        if (!kakaoUserRepository.existsByKakaoId(kakaoInfoDto.getKakaoId().toString())) {
            kakaoUserRepository.save(new KakaoUser(kakaoInfoDto));
            if(lowerAge>=20){
                String year = "1990";
                String birthdayString = year + kakaoInfoDto.getBirthday();
                SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMDD");
                Date birthday = null;
                String password= UUID.randomUUID().toString();
                User user;

                try {
                    birthday=formatter.parse(birthdayString);
                }catch (ParseException e){
                    throw new ApiException(INVALID_FORMAT);
                }
                user = new User(kakaoInfoDto,password, UserRole.USER,s3Uploader.getRandomImage("profile"),birthday);
                userRepository.saveAndFlush(user);
            }else {
                throw new ApiException(NOT_AN_ADULT);
            }
        }

        User user = userRepository.findUserByEmail(kakaoInfoDto.getEmail()).orElseThrow(
                () -> new ApiException(EMAIL_NOT_FOUND)
        );

        String createToken =  jwtUtil.createToken(user, UserRole.USER, "Access");
        String refreshToken = jwtUtil.createToken(user, UserRole.USER, "Refresh");


        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserEmail(user.getEmail());

        if (existingRefreshToken.isPresent()) {
            existingRefreshToken.get().setToken(refreshToken);
            refreshTokenRepository.save(existingRefreshToken.get());
            redisTokenService.storeRefreshToken(user.getEmail(), existingRefreshToken.get().getRefreshToken());
        } else {
            RefreshToken newRefreshToken = new RefreshToken(refreshToken, user.getEmail(), user.getId());
            refreshTokenRepository.save(newRefreshToken);
            redisTokenService.storeRefreshToken(user.getEmail(),newRefreshToken.getRefreshToken());
        }

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", createToken);
        tokens.put("refreshToken", refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonTokens = objectMapper.writeValueAsString(tokens);

        return jsonTokens;
    }

    private String getToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoApiKey);
        body.add("redirect_uri", "https://honsoolzzak.com/api/login");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST, kakaoTokenRequest, String.class);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoInfoDto getUserInfo(String token) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
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

    public ResponseDto kakaoDeleteAccount(String code,HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getToken(code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> responseEntity = rt.exchange(
            "https://kapi.kakao.com/v1/user/unlink",
            HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return ResponseDto.setSuccess("Your account has been successfully deleted");
        } else {
            throw new ApiException(KAKAO_UNLINK_FAILED);
        }

    }


}