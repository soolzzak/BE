package com.example.zzan.global.security.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 500)
    private String refreshToken;

    @NotBlank
    private String userEmail;
    @NotNull
    private Long userId;

    public RefreshToken(String token, String userEmail,Long userId) {
        this.refreshToken = token;
        this.userEmail = userEmail;
        this.userId = userId;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }

	public void setToken(String refreshToken) {
	}
}
