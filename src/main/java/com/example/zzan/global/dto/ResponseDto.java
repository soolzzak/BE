package com.example.zzan.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor (staticName = "set")
public class ResponseDto<T> {

    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResponseDto <T> setSuccess (String message, T data) {
        return ResponseDto.set(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseDto <T> setSuccess (String message) {
        return ResponseDto.set(HttpStatus.OK.value(), message, null);
    }

    public static <T> ResponseDto <T> setBadRequest(String message) {
        return ResponseDto.set(HttpStatus.BAD_REQUEST.value(), message,null);
    }
}
