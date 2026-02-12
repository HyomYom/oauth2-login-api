package com.hyomyang.oauth2login.oauth2.service;

import com.hyomyang.oauth2login.oauth2.domain.User;
import com.hyomyang.oauth2login.oauth2.dto.auth.TokenPair;
import com.hyomyang.oauth2login.oauth2.dto.error.ErrorCode;
import com.hyomyang.oauth2login.oauth2.dto.user.UserResponse;
import com.hyomyang.oauth2login.oauth2.exception.UnauthorizedException;
import com.hyomyang.oauth2login.oauth2.security.jwt.JwtTokenProvider;
import com.hyomyang.oauth2login.oauth2.security.store.RefreshTokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public TokenPair login(String email, String password, String deviceId){
        // 추후 진짜 로그인으로 변경
        User authenticate = userService.authenticate(email, password);
        Long userId = authenticate.getId();
        Set<String> roles = Set.of(authenticate.getRole());

        String access = tokenProvider.createAccessToken(userId, roles);
        String refresh = tokenProvider.createRefreshToken(userId);

        Jws<Claims> refreshJws = tokenProvider.parseToken(refresh);
        refreshTokenStore.saveCurrent(
                userId,
                deviceId,
                tokenProvider.getJti(refreshJws),
                tokenProvider.getExpires(refreshJws)
        );
        return new TokenPair(access, refresh);

    }

    public TokenPair refresh(String refreshToken, String deviceId){
        if(refreshToken == null || refreshToken.isBlank()){
            throw new UnauthorizedException(ErrorCode.TOKEN_INVALID);
        }
        if(deviceId == null || deviceId.isBlank()){
            throw new UnauthorizedException(ErrorCode.DEVICE_ID_REQUIRED);
        }
        Jws<Claims> jws;

        try {
            jws = tokenProvider.parseToken(refreshToken);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_INVALID);
        }

        if(!tokenProvider.isRefresh(jws)){
            throw new UnauthorizedException(ErrorCode.TOKEN_TYPE_MISMATCH);
        }

        Long userId = tokenProvider.getSubject(refreshToken);
        String jti = tokenProvider.getJti(jws);

        if(!refreshTokenStore.isCurrent(userId, deviceId, jti)){
            throw new UnauthorizedException(ErrorCode.REFRESH_REVOKED_OR_REUSED);
        }

        // 새 토큰 생성
        UserResponse response = userService.getById(userId);

        Set<String> roles = new HashSet<>(List.of(response.role()));

        String newAccess = tokenProvider.createAccessToken(userId, roles);
        String newRefresh = tokenProvider.createRefreshToken(userId);

        Jws<Claims> newRefreshJws = tokenProvider.parseToken(newRefresh);

        String newJti = tokenProvider.getJti(newRefreshJws);
        Instant newExpAt = tokenProvider.getExpires(newRefreshJws);

        refreshTokenStore.saveCurrent(userId, deviceId, newJti, newExpAt);

        return new TokenPair(newAccess, newRefresh);

    }

    public void logout(String refreshToken){
        if(refreshToken == null | refreshToken.isBlank()) return;

        try {
            Jws<Claims> jws = tokenProvider.parseToken(refreshToken);

            if(!tokenProvider.isRefresh(jws))return;

            Long userId = tokenProvider.getSubject(refreshToken);
            String jti = tokenProvider.getJti(jws);

            refreshTokenStore.revoke(userId, jti);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
