package com.example.zzan.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {
    private final StringRedisTemplate redisTemplate;

    public RedisTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRefreshToken(String userEmail, String refreshToken) {
        redisTemplate.opsForValue().set(userEmail, refreshToken, 2, TimeUnit.DAYS);
    }

    public Optional<String> retrieveRefreshToken(String userEmail) {
        String token = redisTemplate.opsForValue().get(userEmail);
        return Optional.ofNullable(token);
    }

    public void deleteRefreshToken(String userEmail) {
        redisTemplate.delete(userEmail);
    }
}