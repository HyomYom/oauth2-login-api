package com.hyomyang.oauth2login.oauth2.service;

import com.hyomyang.oauth2login.oauth2.domain.User;
import com.hyomyang.oauth2login.oauth2.dto.error.ErrorCode;
import com.hyomyang.oauth2login.oauth2.dto.user.UserRequest;
import com.hyomyang.oauth2login.oauth2.dto.user.UserResponse;
import com.hyomyang.oauth2login.oauth2.exception.UnauthorizedException;
import com.hyomyang.oauth2login.oauth2.repository.UserRepository;
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
