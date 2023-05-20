package com.example.zzan.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor (staticName = "set")
public class ResponseDto<T> {

    private HttpStatus status;
    private String message;
    private T data;

    public static <T> ResponseDto <T> setSuccess (String message, T data) {
        return ResponseDto.set(HttpStatus.OK, message, data);
    }

    public static <T> ResponseDto <T> setSuccess (String message) {
        return ResponseDto.set(HttpStatus.OK, message, null);
    }
}
