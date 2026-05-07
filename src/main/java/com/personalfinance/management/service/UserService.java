package com.personalfinance.management.service;

import com.personalfinance.management.model.request.RegisterUserRequest;
import com.personalfinance.management.model.request.UpdateUserRequest;
import com.personalfinance.management.model.response.UserResponse;

public interface UserService {
    void register(RegisterUserRequest request);
    UserResponse get();
    UserResponse update(UpdateUserRequest request);
}
