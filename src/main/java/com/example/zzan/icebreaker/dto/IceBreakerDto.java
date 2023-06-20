package com.example.zzan.icebreaker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IceBreakerDto {
    private Long from;
    private String type;
    private String question;
    private Object candidate;
    private Object sdp;

    public IceBreakerDto(Long from, String type, String question, Object candidate, Object sdp) {
        this.from = from;
        this.type = type;
        this.question = question;
        this.candidate = candidate;
        this.sdp = sdp;
    }
}

