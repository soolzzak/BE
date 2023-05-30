package com.example.zzan.user.dto;

import com.example.zzan.user.entity.Gender;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;

    private String email;

    private String nickname;

    private String imgurl;

    private Gender gender;
    private Date birthday;
    private String ageRange;
    public KakaoUserInfoDto(Long id, String email, String nickname, String imgurl, Gender gender,Date birthday,String ageRange)
    {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.imgurl = imgurl;
        this.gender = gender;
        this.birthday = birthday;
        this.ageRange = ageRange;
    }

    public KakaoUserInfoDto(String nickname, Long id) {
        this.nickname = nickname;
        this.id = id;
    }
}