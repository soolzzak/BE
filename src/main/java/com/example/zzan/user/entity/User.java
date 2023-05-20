package com.example.zzan.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String userName;
//
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole admin;

    public User(String email, String password, String userName, UserRole admin) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.admin = admin;
    }
}
