package com.hyomyang.oauth2login.oauth2.component;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TokenExtractor {

    public static String extractBearer(String authorizationHeader){
        if(authorizationHeader == null) return null;
        if(!authorizationHeader.startsWith("Bearer ")) return null;
        String token = authorizationHeader.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
