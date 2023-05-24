package com.example.zzan.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.zzan.follow.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow,Long> {
}
