package com.hyomyang.oauth2login.oauth2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Spring AI Boot Template API",
                version = "V1",
                description = "이직 준비용 Spring Boot + AI 백엔드 템플릿의 REST API 문서"
        )
)
@Configuration
public class OpenApiConfig {
}
