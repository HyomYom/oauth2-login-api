package com.hyomyang.springaiboot.ai.config;

import com.hyomyang.springaiboot.ai.component.ApiAccessDeniedHandler;
import com.hyomyang.springaiboot.ai.component.RestAuthenticationEntryPoint;
import com.hyomyang.springaiboot.ai.filter.JwtAuthenticationFilter;
import com.hyomyang.springaiboot.ai.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtTokenProvider jwtTokenProvider,
                                            RestAuthenticationEntryPoint entryPoint,
                                            ApiAccessDeniedHandler accessDeniedHandler

    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)       // 401
                        .accessDeniedHandler(accessDeniedHandler)    // 403
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/api/auth/**",       // Day12에서 login/refresh/logout 들어갈 자리
                                "/api/users/create"       // Day12에서 login/refresh/logout 들어갈 자리
                        ).permitAll()
                        .requestMatchers("/api/secure/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    };


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
