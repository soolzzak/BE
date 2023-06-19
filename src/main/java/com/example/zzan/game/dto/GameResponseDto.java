package com.example.zzan.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    private Long from;
    private String type;
    private Long data;
    private Object candidate;
    private Object sdp;
}
