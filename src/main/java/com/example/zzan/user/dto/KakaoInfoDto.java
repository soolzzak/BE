package com.example.zzan.user.dto;

import com.example.zzan.user.entity.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class KakaoInfoDto {
    private String username;
    private Long kakaoId;
    private String kakaoImage;
    private String email;
    private Gender gender;
    private String ageRange;
    private String birthday;

    public KakaoInfoDto(String username, Long kakaoId, String kakaoImage, String email, Gender gender, String ageRange, String birthday) {
        this.username = username;
        this.kakaoId = kakaoId;
        this.kakaoImage = kakaoImage;
        this.email = email;
        this.gender = gender;
        this.ageRange = ageRange;
        this.birthday = birthday;
    }
}