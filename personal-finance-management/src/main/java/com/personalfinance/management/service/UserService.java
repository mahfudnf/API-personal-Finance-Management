package com.personalfinance.management.service;

import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.user.RegisterUserRequest;
import com.personalfinance.management.model.user.UpdateUserRequest;
import com.personalfinance.management.model.user.UserResponse;
import com.personalfinance.management.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void register(@Valid RegisterUserRequest request){
        if (userRepository.existsByName(request.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name already registered");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse get(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public UserResponse update(String email,@Valid UpdateUserRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        if (Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return UserResponse.builder().name(user.getName()).build();
    }

}
