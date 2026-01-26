# 🗓 60 Days Backend Challenge

✅ Day 1 – Project Setup

- Spring Boot + Gradle 프로젝트 초기화

- GitHub 저장소 구성 및 기본 환경 설정

✅ Day 2 – Docker Environment

- Docker 기반 MySQL / Adminer 구성

- application-dev.yml로 개발 환경 분리

✅ Day 3 – Git Flow & Collaboration

- Git Flow 브랜치 전략 적용

- GitHub Actions CI 초안 구성

✅ Day 4 – Layered Architecture

- Controller / Service / Repository 계층 분리

- DTO 기반 요청·응답 구조 설계

✅ Day 5 – JPA Entity & DTO Design

- Entity ↔ DTO 분리 패턴 적용

- JPA 연관관계 매핑 (User, Post)

✅ Day 6 – RESTful CRUD APIs

- RESTful CRUD API 구현

- ResponseEntity를 활용한 HTTP 응답 처리

✅ Day 7 – Validation & Exception Handling

- Bean Validation 적용 (@Valid)

- 공통 예외 처리 구조 구성

✅ Day 8 – Common API Response

- ApiResponse<T> 공통 응답 포맷 도입

- 일관된 API 응답 구조 적용

✅ Day 9 – Testing

- Controller / Service 단위 테스트 작성

- 최신 Spring Boot 테스트 스타일 적용

✅ Day 10 – Swagger / OpenAPI Documentation

- SpringDoc(OpenAPI 3) 기반 API 문서화
- Swagger UI를 통한 API 확인 및 테스트
- Controller 단위 API 설명 명시 (@Tag, @Operation)

✅ Day 11 – Spring Security + JWT 인증
- Spring Security 기반 Stateless 인증 구조 설계
- AuthenticationEntryPoint / AccessDeniedHandler로 401/403 응답 표준화
- 공통 응답 포맷(ApiResponse) 적용

✅ Day 12 – Refresh Token 순환 및 재사용 탐지
- Refresh Token 1회성 사용(One-time use) 구조로 개선
- MockMvc 기반 테스트로 정상/비정상 시나리오 검증

✅ Day 13 – JWT 인증 오류 분기 & 권한 인가 구조 정리
- JWT 인증 필터(JwtAuthenticationFilter)에서 토큰 파싱 실패 사유를 request attribute로 기록하도록 개선
- AuthenticationEntryPoint에서 해당 사유를 읽어 ErrorCode 기반의 표준 ErrorResponse 반환

✅ Day 14 – Role 기반 Authorization & 401/403 표준화
- Access Token에 권한(Role)을 포함
- Spring Security가 권한 기반으로 API 접근을 제어 설정

✅ Day 15 – Logout & Token Invalidation Strategy
- Logout API 설계 (Stateless 환경에서의 로그아웃 처리)
- Access Token jti 기반 Redis 블랙리스트 전략 설계
- 로그아웃 시:
  - 현재 Access Token을 Redis에 블랙리스트로 저장 (TTL = 토큰 만료까지)
  - 이후 동일 Access Token 요청 차단
- JWT Filter에서 블랙리스트 토큰 검증 로직 추가
- 로그아웃 이후 API 접근 시 401 Unauthorized 반환

✅ Day 16 – Multi-Device Refresh Token Strategy (B안)
- Refresh Token 디바이스별 관리 전략(B안) 설계
  - X-Device-Id 헤더 기반 세션 식별
  - 디바이스당 Refresh Token 1개만 유효
- Redis 기반 Refresh Token Store 구현
  - rt:cur:{userId}:{deviceId} 키 구조
  - Refresh Token Rotation 시 기존 토큰 자동 무효화
- 로그인 / Refresh 시 deviceId 처리 흐름 정리
  - deviceId가 없을 경우 생성 후 응답 헤더로 반환
- Refresh Token 재사용 / 잘못된 디바이스 접근 시 401 처리

✅ Day 17 – Redis Integration & Security Flow Testing
- Redis를 활용한 인증 상태 관리 통합
  - Refresh Token Store
  - Access Token Blacklist
- Testcontainers 기반 Redis 통합 테스트 구성
  - 테스트 실행 시 Redis 컨테이너 자동 기동
  - 로컬 환경 의존성 제거
- MockMvc 기반 인증 플로우 통합 테스트
  - 로그인 → Refresh Rotation → 이전 Refresh 재사용 차단
  - 로그아웃 → Access Token 재사용 차단
- 인증/인가 시나리오별 테스트 케이스 정리
  - 정상 흐름
  - 만료 토큰
  - 잘못된 deviceId
    - 블랙리스트 토큰