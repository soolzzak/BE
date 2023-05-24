package com.example.zzan.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserRequestDto {

    @NotNull
    private String email;

    @NotNull
    private String password;

    @Column(nullable = false)
    private String username;

    private boolean admin = false;
    private String adminKey = " ";
}
