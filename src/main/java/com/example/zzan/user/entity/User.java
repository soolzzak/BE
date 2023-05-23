package com.example.zzan.user.entity;

import com.example.zzan.user.dto.KakaoUserInfoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@Entity(name = "TB_USER")
@NoArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
//
//    @Column
//    private String loginType;
//
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String providers;

    @Column(nullable = false)
    private String username;

    @Column(nullable = true)
    private String imgurl;

    private Long kakaoId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(nullable = true)
    private String img;

    public User(String email, String password, String username, UserRole role,String providers,String imgurl) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.providers =providers;
        this.img = imgurl;
    }

    public User(String username, String img) {
        this.username=username;
        this.img = img;
    }

    public void UserImg(String img){
        this.img = img;
    }

    public void username(String username){
        this.username = username;
    }
    public static class ProvidersList {
        public static final String SOOLZZAK = "SOOLZZAK";
        public static final String KAKAO = "KAKAO";
    }

    public User(KakaoUserInfoDto kakaoUserInfoDto) {
        this.username = kakaoUserInfoDto.getNickname();
        this.kakaoId = kakaoUserInfoDto.getId();
        this.email = kakaoUserInfoDto.getEmail();
        this.imgurl = kakaoUserInfoDto.getImgurl();
    }

}
