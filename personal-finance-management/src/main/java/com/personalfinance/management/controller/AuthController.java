package com.personalfinance.management.controller;

import com.personalfinance.management.model.user.LoginUserRequest;
import com.personalfinance.management.model.TokenResponse;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(
            path = "/api/users/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody @Valid LoginUserRequest request){
        TokenResponse tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

    @PostMapping(
            path = "/api/users/logout"
    )
    public WebResponse<String> logout(){
        return WebResponse.<String>builder().data("OK").build();
    }
}
