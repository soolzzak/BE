package com.example.zzan.global.security.repository;


import com.example.zzan.global.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByUserEmail(String userEmail);
    Optional<RefreshToken> findRefreshTokenByUserId(Long userId);
}
