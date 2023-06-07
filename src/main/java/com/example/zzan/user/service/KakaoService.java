package com.example.zzan.user.service;

import com.example.zzan.global.util.JwtUtil;
import com.example.zzan.user.dto.KakaoUserInfoDto;
import com.example.zzan.user.entity.Gender;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;
import com.example.zzan.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private boolean ageCheck(String ageRange){
        String[] age = ageRange.split("~");

        if(Integer.parseInt(age[0]) < 20)
            return false;
        else
            return true;
    }

    public String kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getToken(code);

        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        if(ageCheck(kakaoUserInfo.getAgeRange()) == false)
            return "FAILED";

        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        String createToken =  jwtUtil.createToken(kakaoUser, kakaoUser.getRole(),"Access");

        return createToken;
    }
    private String getToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "b39a9a7ab117d1d1c9ca71fa61285f13");
        body.add("redirect_uri", "http://localhost:8080/api/login/oauth2/code/kakao");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String imgurl = jsonNode.get("properties")
                .get("thumbnail_image").asText();
        Gender gender = jsonNode.get("kakao_account")
                .get("gender").asText()  == "female" ? Gender.FEMALE : Gender.MALE;
        String birthday = jsonNode.get("kakao_account")
                .get("birthday").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String ageRange = jsonNode.get("kakao_account")
                .get("age_range").asText();

        String birthdayMon = birthday.substring(0,2);
        String birthdayDay = birthday.substring(2);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse("0000-"+birthdayMon+"-"+birthdayDay);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        log.info("카카오 사용자 정보: " + id + "," + email);
        return new KakaoUserInfoDto(id,  email,nickname,imgurl,gender,date,ageRange);
    }

    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findUserByEmail(kakaoUserInfo.getEmail())
                .orElse(null);
        if (kakaoUser == null) {
            String kakaoEmail = kakaoUserInfo.getEmail();
            User sameEmailUser = userRepository.findUserByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String email = kakaoUserInfo.getEmail();
                kakaoUser = new User(email,
                        encodedPassword,
                        kakaoUserInfo.getNickname(),
                        UserRole.USER,
                        User.ProvidersList.KAKAO,
                        kakaoUserInfo.getImgurl(),
                        kakaoUserInfo.getGender(),
                        kakaoUserInfo.getBirthday()
                );
            }
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}