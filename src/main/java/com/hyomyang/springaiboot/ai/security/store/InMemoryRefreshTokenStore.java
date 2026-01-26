package com.hyomyang.springaiboot.ai.security.store;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore{

    // key: userId:jti
    private final Map<String, Instant> store = new ConcurrentHashMap<>();

    private String key(Long userId, String jti){
        return userId + ":" + jti;
    }

    @Override
    public void save(Long userId, String jti, Instant expiresAt) {
        store.put(key(userId, jti), expiresAt);

    }

    @Override
    public boolean exists(Long userId, String jti) {
        Instant exp = store.get(key(userId, jti));
        if(exp == null) return false;

        // 만료된 건 자동 제거(메모리 정리)
        if (exp.isBefore(Instant.now())) {
            store.remove(key(userId, jti));
            return false;
        }
        return true;
    }

    @Override
    public void revoke(Long userId, String jti) {
        store.remove(key(userId, jti));

    }

    @Override
    public void saveCurrent(Long userId, String deviceId, String jti, Instant expiresAt) {

    }

    @Override
    public boolean isCurrent(Long userId, String deviceId, String jti) {
        return false;
    }

    @Override
    public void revokeDevice(Long userId, String deviceId) {

    }

    @Override
    public void revokeAll(Long userId) {
        String prefix = userId + ":";
        store.keySet().removeIf(key -> key.startsWith(prefix));

    }

    @Override
    public void markRevoked(Long userId, String jti, Instant expiresAt) {

    }

    @Override
    public boolean isRevoked(Long userId, String jti) {
        return false;
    }
}
