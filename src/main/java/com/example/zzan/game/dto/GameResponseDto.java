package com.example.zzan.game.dto;

import lombok.*;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class GameResponseDto {
    private Long from;
    private String type;
    private String idiom;
    private Object candidate;
    private Object sdp;


//    public GameResponseDto(String idiom){
//        this.idiom = idiom;
//    }
}
