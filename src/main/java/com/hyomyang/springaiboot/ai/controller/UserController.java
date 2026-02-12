package com.hyomyang.springaiboot.ai.controller;

import com.hyomyang.springaiboot.ai.dto.response.ApiResponse;
import com.hyomyang.springaiboot.ai.dto.user.UserRequest;
import com.hyomyang.springaiboot.ai.dto.user.UserResponse;
import com.hyomyang.springaiboot.ai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name ="User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
//@RequiredArgsConstructor 생성자 대체
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "사용자 생성",
            description = "새로운 사용자를 생성하고 생성된 사용자 정보를 반환"
    )
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> create (@Valid @RequestBody UserRequest req) {
        UserResponse created = userService.create(req);
        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    @Operation(
            summary = "ID로 사용자 조회",
            description = "사용자 ID로 사용자를 단건 조회"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        UserResponse user = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}
