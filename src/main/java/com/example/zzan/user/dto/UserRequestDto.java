package com.example.zzan.user.dto;

import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.exception.ExceptionEnum;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserRequestDto {

    private String email;

    private String password;

    private String username;

    private String birthday;

    private String gender;

    private boolean admin = false;
    private String adminKey = " ";

    public Date getBirthday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(birthday);
        } catch (ParseException e) {
           throw new ApiException(ExceptionEnum.INVALID_BIRTHDAY);
        }
    }
}
