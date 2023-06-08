package com.example.zzan.user.dto;

import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.user.entity.User;
import lombok.Getter;

@Getter
public class KakaoResponseDto {
    TokenDto tokenDto;
    User user;

    public KakaoResponseDto(TokenDto tokenDto, User user) {
        this.tokenDto = tokenDto;
        this.user = user;
    }
}
