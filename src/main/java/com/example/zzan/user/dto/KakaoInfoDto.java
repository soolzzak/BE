package com.example.zzan.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoInfoDto {
    private String username;
    private Long kakaoId;

    public KakaoInfoDto(String username, Long kakaoId){
        this.username = username;
        this.kakaoId = kakaoId;
    }
}
