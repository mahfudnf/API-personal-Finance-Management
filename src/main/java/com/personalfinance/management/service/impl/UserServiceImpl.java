package com.personalfinance.management.service.impl;

import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.exception.custom.BadRequestException;
import com.personalfinance.management.model.request.RegisterUserRequest;
import com.personalfinance.management.model.request.UpdateUserRequest;
import com.personalfinance.management.model.response.UserResponse;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.UserService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponseUtils responseUtils;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ResponseUtils responseUtils){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.responseUtils = responseUtils;
    }

    @Override
    public void register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email sudah terdaftar");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    @Override
    public UserResponse get() {
        String email = responseUtils.getCurrentUserEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {
                });

        return responseUtils.toUserResponse(user);
    }

    @Override
    public UserResponse update( UpdateUserRequest request) {
        String email = responseUtils.getCurrentUserEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {
                });

        if (Objects.nonNull(request.getFirstName())){
            user.setFirstName(request.getFirstName());
        }
        if (Objects.nonNull(request.getLastName())){
            user.setLastName(request.getLastName());
        }
        if (Objects.nonNull(request.getPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return responseUtils.toUserResponse(user);
    }
}
