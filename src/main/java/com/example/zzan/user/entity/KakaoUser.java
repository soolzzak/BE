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

    public KakaoUser(KakaoInfoDto kakaoInfoDto) {
        this.id = kakaoInfoDto.getKakaoId();
        this.username = kakaoInfoDto.getUsername();
        this.kakaoId = kakaoInfoDto.getKakaoId().toString();
    }
}
