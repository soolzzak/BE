package com.example.zzan.global.security.repository;


import com.example.zzan.global.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//    @Query("SELECT r FROM RefreshToken r WHERE r.userId = :userId")
//    Optional<RefreshToken> findUserByUserEmail(@Param("user") String userId);

//    Optional<RefreshToken> findByUsername(String username);

    Optional<RefreshToken> findRefreshTokenByUserEmail(String userEmail);
}
