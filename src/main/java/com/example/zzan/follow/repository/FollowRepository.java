package com.example.zzan.follow.repository;

import java.util.Optional;

import com.example.zzan.follow.entity.Follow;
import com.example.zzan.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow,Long> {

	Optional<Follow> findByFollowingIdAndFollowerId(User following, User follower);
}
