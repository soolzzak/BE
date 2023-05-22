package com.example.zzan.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserRequestDto {

    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$", message = "이메일 형식에 맞춰 작성해주세요.")
    @NotNull(message = "E-mail 주소를 입력해주세요.")
    private String email;

    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자에서 15 사이로만 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z\\p{Punct}0-9]*$", message = "비밀번호는 알파벳 대소문자, 특수문자, 숫자만 가능합니다.")
    @NotNull(message =  "비밀번호를 입력해주세요.")
    private String password;

    private String username;

    private String admin;
}
