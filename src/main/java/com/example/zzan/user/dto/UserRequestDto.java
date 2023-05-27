package com.example.zzan.user.dto;

import com.example.zzan.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserRequestDto {

    private String email;

    private String password;

    private String username;

    private Date birthday;

    private Gender gender;

    private boolean admin = false;
    private String adminKey = " ";

}
