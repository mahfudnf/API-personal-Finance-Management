package com.personalfinance.management.controller;

import com.personalfinance.management.model.request.RegisterUserRequest;
import com.personalfinance.management.model.request.UpdateUserRequest;
import com.personalfinance.management.model.response.UserResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
            path = "/api/users/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> register(@RequestBody @Valid RegisterUserRequest request){
        userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<String>builder().data("OK").build());
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserResponse>> get(){
        UserResponse response = userService.get();

        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<UserResponse>builder().data(response).build());
    }

    @PatchMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserResponse>> update(@RequestBody @Valid UpdateUserRequest request){
        UserResponse response = userService.update(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<UserResponse>builder().data(response).build());
    }
}
