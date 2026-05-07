package com.personalfinance.management.service.impl;

import com.personalfinance.management.model.request.LoginUserRequest;
import com.personalfinance.management.model.response.TokenResponse;
import com.personalfinance.management.service.AuthService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final ResponseUtils responseUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager, ResponseUtils responseUtils) {
        this.authenticationManager = authenticationManager;
        this.responseUtils = responseUtils;
    }

    @Override
    public TokenResponse login(LoginUserRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return responseUtils.toTokenResponse(userDetails);
    }
}
