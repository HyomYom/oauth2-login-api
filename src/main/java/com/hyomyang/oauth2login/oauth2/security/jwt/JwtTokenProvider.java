package com.hyomyang.oauth2login.oauth2.security.jwt;

import com.hyomyang.oauth2login.oauth2.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final JwtProperties jwtProps;
    private final Clock clock;


    public JwtTokenProvider(
            @Qualifier("jwtSecretKey") SecretKey key,
            JwtProperties jwtProps,
            Clock clock
            ) {
        this.key = key;
        this.jwtProps = jwtProps;
        this.clock = clock;
    }

    public String createAccessToken(Long userId, Set<String> roles) {
        String jti = UUID.randomUUID().toString();
        Instant now = Instant.now(clock);
        Instant exp = now.plus(jwtProps.accessTokenExpMin(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(String.valueOf(userId)) //sub claim
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("type","access")
                .claim("roles", roles)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        String jti = UUID.randomUUID().toString();
        Instant now = Instant.now(clock);
        Instant exp = now.plus(jwtProps.refreshTokenExpDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .id(jti)   // 로데이션/폐기 관리에 유용
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("type", "refresh")
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }


    // == getUserId
    public Long getSubject(String token){
        String sub = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.parseLong(sub);
    }

    public String getJti(Jws<Claims> jws) {
        return jws.getPayload().getId();
    }

    public Instant getExpires(Jws<Claims> jws) {
        return jws.getPayload().getExpiration().toInstant();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRoles(Jws<Claims> jws) {
        Object roles = jws.getPayload().get("roles");

        if(roles == null) return Set.of("ROLE_USER");
        if(roles instanceof List<?> list){
            return list.stream().map(String::valueOf).collect(Collectors.toSet());
        }

        return Set.of(String.valueOf(roles));
    }

    public boolean validate(String token){
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean isRefresh(Jws<Claims> jws){
        return "refresh".equals(jws.getPayload().get("type", String.class));
    }

}
