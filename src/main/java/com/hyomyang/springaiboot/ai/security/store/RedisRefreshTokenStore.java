package com.hyomyang.springaiboot.ai.security.store;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Primary //InMemory 구현체 대신 Redis 구현체를 기본으로 선택
@Component
@AllArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private final StringRedisTemplate redis;

    private String curKey(Long userId, String deviceId){
        return "rt:cur:" + userId + ":" + deviceId;
    }

    private String revKey(Long userId, String jti){
        return "rt:rev:" + userId + ":" + jti;
    }

    @Override
    public void saveCurrent(Long userId, String deviceId, String jti, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if(ttl.isNegative() || ttl.isZero()) return;

        redis.opsForValue().set(curKey(userId,deviceId), jti, ttl);

    }

    @Override
    public boolean isCurrent(Long userId, String deviceId, String jti) {
        String current = redis.opsForValue().get(curKey(userId,deviceId));
        return current != null && current.equals(jti);
    }

    @Override
    public void revokeDevice(Long userId, String deviceId) {
        redis.delete(curKey(userId,deviceId));

    }

    @Override
    public void revokeAll(Long userId) {
        String pattern  = "rt:cur:" + userId + ":*";
        var keys = redis.keys(pattern);
        if(keys != null && !keys.isEmpty()) redis.delete(keys);

    }

    @Override
    public void markRevoked(Long userId, String jti, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if(ttl.isNegative() || ttl.isZero()) return;
        redis.opsForValue().set(revKey(userId,jti), "1", ttl);
    }

    @Override
    public boolean isRevoked(Long userId, String jti) {
        Boolean hasKey = redis.hasKey(revKey(userId,jti));
        return Boolean.TRUE.equals(hasKey);
    }


    @Override
    public void save(Long userId, String jti, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);

        if(ttl.isNegative() || ttl.isZero()) return;

        redis.opsForValue().set(curKey(userId, jti), "1", ttl);
    }

    @Override
    public boolean exists(Long userId, String jti) {
        Boolean hasKey = redis.hasKey(curKey(userId, jti));

        return Boolean.TRUE.equals(hasKey);
    }

    @Override
    public void revoke(Long userId, String jti) {
        redis.delete(curKey(userId, jti));
    }
//
//    @Override
//    public void revokeAll(Long userId) {
//        String pattern = "rt:"+userId+":*";
//        var keys = redis.keys(pattern);
//        if(keys != null && !keys.isEmpty()){
//            redis.delete(keys);
//        }
//
//    }
}
