package com.example.zzan.alcohol.repository;

import com.example.zzan.alcohol.entity.Alcohol;
import com.example.zzan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlcoholRepository extends JpaRepository<Alcohol, Long> {
    Optional<Alcohol> findByUser(User user);
}
