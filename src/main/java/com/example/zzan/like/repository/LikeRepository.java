package com.example.zzan.like.repository;

import com.example.zzan.like.entity.Like;
import com.example.zzan.like.entity.LikeEnum;
import com.example.zzan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Like findByUserAndTargetUserAndLikeEnum(User user, User targetUser, LikeEnum likeEnum);
}
