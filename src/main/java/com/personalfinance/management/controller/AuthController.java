package com.personalfinance.management.controller;

import com.personalfinance.management.model.request.LoginUserRequest;
import com.personalfinance.management.model.response.TokenResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            path = "/api/users/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<TokenResponse>> login(@RequestBody @Valid LoginUserRequest request){
        TokenResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<TokenResponse>builder().data(response).build());
    }

    @PostMapping(
            path = "/api/users/logout"
    )
    public ResponseEntity<WebResponse<String>> logout(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<String>builder().data("OK").build());
    }
}
