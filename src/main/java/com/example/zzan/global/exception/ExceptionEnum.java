package com.example.zzan.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {
    // 400 Bad_Request
    INVALID_LOGIN(HttpStatus.BAD_REQUEST.value(), "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호는 8~15자리, a-z, A-Z, 숫자, 특수문자 조합으로 구성되어야 합니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST.value(), "관리자 암호를 잘못 입력하셨습니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "토큰값을 찾을 수 없습니다."),
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST.value(), "전달 값이 잘못 되었습니다"),
    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(),  "권한이 없습니다."),
    NOT_ALLOWED(HttpStatus.UNAUTHORIZED.value(), "로그인 후 이용가능합니다."),
    // 404 Not Found
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND.value(),  "방이 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "아이디가 존재하지 않습니다."),
    // 409 Conflict
    USERS_DUPLICATION(HttpStatus.CONFLICT.value(), "중복된 이메일이 존재합니다."),
    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에러가 발생했습니다.");

    private final int status;
    private final String message;

    ExceptionEnum(int status, String message){
        this.status = status;
        this.message = message;
    }
}
