package com.example.zzan.follow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.zzan.follow.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow,Long> {
	Optional<Follow> findByFollowingUserEmailAndUserEmail(String followingUserEmail, String email);
}
