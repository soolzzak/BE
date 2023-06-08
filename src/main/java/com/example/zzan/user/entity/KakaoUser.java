package com.example.zzan.user.entity;

import com.example.zzan.user.dto.KakaoInfoDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class KakaoUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String kakaoId;

    @Column(nullable = false)
    private String username;

    @Column
    private String kakaoImage;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String ageRange;

    @Column
    private String birthday;

    public KakaoUser(KakaoInfoDto kakaoInfoDto) {
        this.id = kakaoInfoDto.getKakaoId();
        this.username = kakaoInfoDto.getUsername();
        this.kakaoId = kakaoInfoDto.getKakaoId().toString();
        this.kakaoImage = kakaoInfoDto.getKakaoImage();
        this.email = kakaoInfoDto.getEmail();
        this.gender = kakaoInfoDto.getGender();
        this.ageRange = kakaoInfoDto.getAgeRange();
        this.birthday = kakaoInfoDto.getBirthday();
    }
}
