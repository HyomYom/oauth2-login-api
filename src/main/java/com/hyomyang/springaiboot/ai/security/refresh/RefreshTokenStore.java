package com.hyomyang.springaiboot.ai.security.refresh;

import java.time.Instant;

public interface RefreshTokenStore {
    void save(Long userId, String jti, Instant expiresAt);
    boolean exists(Long userId, String jti);
    void revoke(Long userId, String jti);
    void revokeAll(Long userId);
}
