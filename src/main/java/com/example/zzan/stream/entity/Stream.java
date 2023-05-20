package com.example.zzan.stream.entity;

import com.example.zzan.stream.dto.StreamRequestDto;
import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Long id;

    private String title;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public Stream(StreamRequestDto streamRequestDto){
        this.title = streamRequestDto.getTitle();
    }

//    public void update (StreamRequestDto streamRequestDto){
//
//    }

}
