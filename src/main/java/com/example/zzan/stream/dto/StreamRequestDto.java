package com.example.zzan.stream.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamRequestDto {

    private Long streamdId;
    private String title;
    private String username;
    private String image;
    private String category;

}
