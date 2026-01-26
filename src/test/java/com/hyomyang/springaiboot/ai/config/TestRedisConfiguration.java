package com.hyomyang.springaiboot.ai.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Redis Testcontainer 설정
 *
 * ⭐ Testcontainers에는 독립적인 'redis' 모듈이 없습니다!
 * GenericContainer를 사용해서 Redis 컨테이너를 생성합니다.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestRedisConfiguration {

    /**
     * Redis Testcontainer 생성
     * Spring Boot 3.1+의 ServiceConnection을 사용하면
     * 자동으로 spring.data.redis 설정이 적용됩니다
     */
    @Bean
    @ServiceConnection(name = "redis")
    public GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6479)
                .withReuse(true);  // 여러 테스트에서 재사용
    }
}