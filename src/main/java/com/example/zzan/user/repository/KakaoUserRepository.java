package com.example.zzan.user.repository;

import com.example.zzan.user.entity.KakaoUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoUserRepository extends JpaRepository<KakaoUser, Long> {
    boolean existsByKakaoId(String kakaoId);
}
