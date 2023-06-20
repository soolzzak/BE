package com.example.zzan.user.dto;

import java.util.Date;

import com.example.zzan.user.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DeleteAccountRequestDto {
	private String password;
}

