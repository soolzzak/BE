package com.example.zzan.like.entity;

import com.example.zzan.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "TB_LIKE")
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "TARGET_ID")
    private User targetUser;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LikeEnum likeEnum;

    public Like(User user, User targetUser, LikeEnum likeEnum) {
        this.user = user;
        this.targetUser = targetUser;
        this.likeEnum = likeEnum;
    }

}
