package com.example.zzan.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameResponseDto {
    private String idiom;

    public GameResponseDto(String idiom){
        this.idiom = idiom;
    }
}
