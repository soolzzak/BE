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
    private Long userId;
//
//    @Column
//    private String loginType;
//
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;
//
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole admin;

    @Column(nullable = true)
    private String img;

    @Column(nullable = true)
    private String nickname;

    public User(String email, String password, String username, UserRole admin) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.admin = admin;
    }

    public User(String nickname, String img) {
        this.nickname=nickname;
        this.img = img;
    }


    public void Userimg(String img){
        this.img = img;
    }

    public void Usernickname(String nickname){
        this.nickname = nickname;
    }


}
