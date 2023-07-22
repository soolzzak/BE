package com.example.zzan.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GameResponseDto {
    private Long from;
    private String type;
    private String word;
    private int count;
    private int startCount;
    private Object candidate;
    private Object sdp;

    public GameResponseDto(Long from, String type, String word, int count, Object candidate, Object sdp) {
        this.from = from;
        this.type = type;
        this.word = word;
        this.count = count;
        this.candidate = candidate;
        this.sdp = sdp;
    }

    public GameResponseDto(Long from, String type, int startCount, Object candidate, Object sdp) {
        this.from = from;
        this.type = type;
        this.startCount = startCount;
        this.candidate = candidate;
        this.sdp = sdp;
    }

    public GameResponseDto(Long from, String type, String word, Object candidate, Object sdp) {
        this.from = from;
        this.type = type;
        this.word = word;
        this.candidate = candidate;
        this.sdp = sdp;
    }
}
