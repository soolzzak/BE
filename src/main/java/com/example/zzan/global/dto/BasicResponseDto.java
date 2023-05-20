package com.example.zzan.global.dto;

import com.example.zzan.global.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor (staticName = "add")
public class BasicResponseDto {
    private int statusCode;
    private String message;

    public static BasicResponseDto setSuccess(StatusEnum statusEnum, String message) {
        return BasicResponseDto.add(statusEnum.getStatusCode(), message);
    }

    public static BasicResponseDto addBadRequest(String message) {
        return BasicResponseDto.add(StatusEnum.BAD_REQUEST.getStatusCode(), message);
    }

}
