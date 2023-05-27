package com.example.zzan.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호를 재설정 해주세요. 비밀번호는 8~15자리, a-z, A-Z, 숫자, 특수문자 조합으로 구성되어야 합니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST.value(), "이메일 형식에 맞춰 작성해주세요."),
    INVALID_BIRTHDAY(HttpStatus.BAD_REQUEST.value(), "생년월일을 지정해주세요."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST.value(), "성별을 지정해주세요."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST.value(), "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_ADMIN_INPUT(HttpStatus.BAD_REQUEST.value(), "관리자 암호를 잘못 입력하셨습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT.value(), "이미 가입되어있는 이메일입니다."),
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "ACCESS 토큰값이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "REFRESH 토큰값이 만료되었습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED.value(),"유효하지 않은 JWT 서명 입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(),"지원되지 않는 JWT 토큰 입니다."),
    EMPTY_JWT_CLAIMS(HttpStatus.UNAUTHORIZED.value(),"잘못된 JWT 토큰 입니다."),

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED.value(),  "사용자 권한이 없습니다."),
    NOT_ALLOWED(HttpStatus.UNAUTHORIZED.value(), "로그인 후 이용가능합니다."),
    NOT_ALLOWED_USERNAME(HttpStatus.BAD_REQUEST.value(),"아이디에 사용할 수 없는 단어가 있습니다."),
    NOT_ALLOWED_SELF_LIKE(HttpStatus.BAD_REQUEST.value(), "스스로의 도수를 수정할 수 없습니다."),
    NOT_ALLOWED_SELF_FOLLOW(HttpStatus.BAD_REQUEST.value(), "스스로 팔로우 기능을 사용할 수 없습니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND.value(),  "방이 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "아이디가 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "이메일이 존재하지 않습니다."),
    TARGET_USER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "해당 사용자를 찾을 수 없습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST.value(), "파일 형식이 잘못되었습니다."),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST.value(),"이미 팔로우를 하고 있습니다.");

    private final int status;
    private final String message;

    ExceptionEnum(int status, String message){
        this.status = status;
        this.message = message;
    }
}
