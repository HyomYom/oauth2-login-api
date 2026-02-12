package com.hyomyang.oauth2login.oauth2.config.properties;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Base64;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        @Positive int accessTokenExpMin,
        @Positive int refreshTokenExpDays,
        @NotBlank String hmacSecretBase64
) {
    public JwtProperties{
        byte[] decoded = Base64.getDecoder().decode(hmacSecretBase64);
        if(decoded.length < 32){
            throw new IllegalArgumentException(
                    "jwt.secret(Base64 디코딩 후)가 32바이트 이상이어야 합니다. 현재: " + decoded.length + "바이트"
            );
        }
    }

    public byte[] secretKeyBytes(){
        return Base64.getDecoder().decode(hmacSecretBase64);
    }
}
