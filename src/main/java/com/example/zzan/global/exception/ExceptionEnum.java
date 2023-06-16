package com.example.zzan.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "Please reset your password. Passwords must be 8-15 characters long and contain a combination of lowercase letters, uppercase letters, numbers, and special characters."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST.value(), "Please enter a valid email address."),
    INVALID_BIRTHDAY(HttpStatus.BAD_REQUEST.value(), "Please specify your date of birth."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST.value(), "Please specify your gender."),
    NOT_ALLOWED_USERNAME(HttpStatus.BAD_REQUEST.value(),"The username contains forbidden words. Please choose a different username."),
    NOT_ALLOWED_ROOMTITLE(HttpStatus.BAD_REQUEST.value(), "The room title contains forbidden words. Please use different room title."),
    REQUIRE_PASSWORD(HttpStatus.BAD_REQUEST.value(), "Please set password to create a private room."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST.value(), "The username or password is incorrect."),
    INVALID_ADMIN_INPUT(HttpStatus.BAD_REQUEST.value(), "Incorrect administrator password input."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "The passwords do not match."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT.value(), "The email address is already registered."),
    NOT_ALLOWED(HttpStatus.UNAUTHORIZED.value(), "This action is only allowed for logged-in users."),
    INVALID_FILE(HttpStatus.BAD_REQUEST.value(), "The file format is incorrect."),
    INVALID_FILE_CONVERSION(HttpStatus.BAD_REQUEST.value(), "MultipartFile -> File Convert Fail."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED.value(), "You do not have permission to access this resource."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST.value(), "The input does not match the required format."),
    NOT_AN_ADULT(HttpStatus.BAD_REQUEST.value(), "Cannot register because you are not an adult."),
    REPORT_NOT_REASONABLE(HttpStatus.BAD_REQUEST.value(), "The reason for reporting is not appropriate."),

    TARGET_USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "The specified user cannot be found."),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST.value(),"You are already following this user."),
    FAILED_SEND_MAIL(HttpStatus.BAD_REQUEST.value(), "Failed to send the verification email."),
    ROOM_ALREADY_FULL(HttpStatus.BAD_REQUEST.value(), "Another user has already joined the room."),

    BLOCKED_USER(HttpStatus.BAD_REQUEST.value(), "This user is blocked."),
    USER_NOT_IN_ROOM(HttpStatus.NOT_FOUND.value(), "The user is not in the room."),

    NOT_ALLOWED_SELF_LIKE(HttpStatus.BAD_REQUEST.value(), "You cannot modify your own likes."),
    NOT_ALLOWED_SELF_FOLLOW(HttpStatus.BAD_REQUEST.value(), "You cannot follow yourself."),
    NOT_ALLOWED_SELF_REPORT(HttpStatus.BAD_REQUEST.value(), "You cannot report yourself."),
    NOT_ALLOWED_SELF_BLOCK(HttpStatus.BAD_REQUEST.value(), "You cannot block yourself."),

    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "The room does not exist."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "The username does not exist."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "The email address does not exist."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "The image does not exist."),
    URL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "URL is not available."),

    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "The ACCESS token has expired."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "The REFRESH token has expired."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT signature."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Unsupported JWT token."),
    EMPTY_JWT_CLAIMS(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token."),

    SEARCH_FAILED(HttpStatus.NOT_FOUND.value(), "Invalid search format."),
    ONLY_HOST_CAN_KICK(HttpStatus.FORBIDDEN.value(), "Only the host can kick users out.");

    private final int status;
    private final String message;

    ExceptionEnum(int status, String message){
        this.status = status;
        this.message = message;
    }
}
