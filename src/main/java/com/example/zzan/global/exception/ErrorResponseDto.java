package com.example.zzan.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private int status;
    private String message;

    public ErrorResponseDto (ExceptionEnum exceptionEnum){
        this.status = exceptionEnum.getStatus();
        this.message = exceptionEnum.getMessage();
    }
}
