package com.example.zzan.follow.repository;

import com.example.zzan.follow.dto.FollowResponseDto;
import com.example.zzan.follow.entity.Follow;
import com.example.zzan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    Optional<Follow> findByFollowingUserAndFollowerUser(User user, User user1);
    List<Follow> findByFollowingUser(User followingUser);

    List<Follow> findAllByFollowerUser(User User);

    // List<FollowResponseDto> findAllById(Long id);
}
