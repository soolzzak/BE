package com.example.zzan.global.exception;

import lombok.Getter;

@Getter

public class ApiException extends RuntimeException{

    private final ExceptionEnum exceptionEnum;

    public ApiException (ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }
}
