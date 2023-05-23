package com.example.zzan.user.entity;

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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(nullable = true)
    private String img;

    public User(String email, String password, String username, UserRole role,String providers) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.providers =providers;
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
}
