package com.example.zzan.game.dto;

import lombok.*;


@NoArgsConstructor
@Data
public class GameResponseDto {
    private Long from;
    private String type;
    private String idiom;
    private int count;
    private Object candidate;
    private Object sdp;

    public GameResponseDto(Long from, String type, String idiom, Object candidate, Object sdp) {
        this.from = from;
        this.type = type;
        this.idiom = idiom;
        this.candidate = candidate;
        this.sdp = sdp;
    }

    public GameResponseDto(Long from, String type, int count, Object candidate, Object sdp) {
        this.from = from;
        this.type = type;
        this.count = count;
        this.candidate = candidate;
        this.sdp = sdp;
    }
}
