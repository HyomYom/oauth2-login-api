package com.hyomyang.springaiboot.ai.service;

import com.hyomyang.springaiboot.ai.domain.User;
import com.hyomyang.springaiboot.ai.dto.error.ErrorCode;
import com.hyomyang.springaiboot.ai.dto.user.UserRequest;
import com.hyomyang.springaiboot.ai.dto.user.UserResponse;
import com.hyomyang.springaiboot.ai.exception.UnauthorizedException;
import com.hyomyang.springaiboot.ai.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;


    public UserResponse create(UserRequest req){
        String hashedPassword = passwordEncoder.encode(req.password());
        User user = repo.save(new User(req.email(), hashedPassword, req.name(), req.role(),req.active()));

        return UserResponse.of(user);

    }

    public UserResponse getById(Long id){
        User u = repo.findById(id).orElseThrow(()->new RuntimeException("user not found"));
        return UserResponse.of(u, u.getRole());
    }

    public User authenticate(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new UnauthorizedException(ErrorCode.LOGIN_FAILED);
        }


        User user = repo.findByEmail(email).orElseThrow(() -> new UnauthorizedException(ErrorCode.LOGIN_FAILED));


        if(!user.isActive()){
            throw new UnauthorizedException(ErrorCode.USER_INACTIVE);
        }

        if(!passwordEncoder.matches(password, user.getPasswordHash())){
            throw new UnauthorizedException(ErrorCode.LOGIN_FAILED);
        }
        return user;
    }
}
