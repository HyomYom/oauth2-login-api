package com.hyomyang.oauth2login.oauth2.security.store;

import java.time.Instant;

public interface RefreshTokenStore {
    void save(Long userId, String jti, Instant expiresAt);
    boolean exists(Long userId, String jti);
    void revoke(Long userId, String jti);
//    void revokeAll(Long userId);

    void saveCurrent(Long userId, String deviceId, String jti, Instant expiresAt);
    boolean isCurrent(Long userId, String deviceId, String jti);
    void revokeDevice(Long userId, String deviceId);         // 해당 디바이스 세션 종료
    void revokeAll(Long userId);                             // 전체 로그아웃(선택)
    void markRevoked(Long userId, String jti, Instant expiresAt); // 선택
    boolean isRevoked(Long userId, String jti);
}
