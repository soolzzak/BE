package com.example.zzan.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity(name = "TB_USER")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

//    @Column
//    private String loginType;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(nullable = true)
    private String img;

    @ColumnDefault("14")
    private int alcohol;

    public User(String email, String password, String username, UserRole role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public User(String username, String img) {
        this.username = username;
        this.img = img;
    }

    public void UserImg(String img) {
        this.img = img;
    }

    public void username(String username) {
        this.username = username;
    }

    public void updateAlcohol(boolean likeOrDislike) {
        this.alcohol = likeOrDislike ? this.alcohol + 1 : this.alcohol - 1;
    }
}
