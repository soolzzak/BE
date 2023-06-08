package com.example.zzan.user.repository;

import com.example.zzan.user.entity.KakaoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoUserRepository extends JpaRepository<KakaoUser, Long> {
    boolean existsByKakaoId(String kakaoId);
    Optional<KakaoUser> findByKakaoId(String kakaoId);
    Optional<KakaoUser> findByUsername(String username);
}
