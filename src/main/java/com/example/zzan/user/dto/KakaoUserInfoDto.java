package com.example.zzan.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;

    private String email;

    private String nickname;

    private String imgurl;

    public KakaoUserInfoDto(Long id, String email, String nickname,String imgurl){
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.imgurl = imgurl;
    }

    public KakaoUserInfoDto(String nickname, Long id) {
        this.nickname = nickname;
        this.id = id;
    }
}