package com.example.zzan.alcohol.entity;

import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "TB_ALCOHOL")
@NoArgsConstructor
public class Alcohol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALCOHOL_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public Alcohol(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getId();
    }
}
