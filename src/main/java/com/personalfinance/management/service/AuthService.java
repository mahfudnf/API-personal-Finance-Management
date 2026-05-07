package com.personalfinance.management.service;

import com.personalfinance.management.model.request.LoginUserRequest;
import com.personalfinance.management.model.response.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginUserRequest request);
}
