package com.personalfinance.management.controller;

import com.personalfinance.management.model.user.RegisterUserRequest;
import com.personalfinance.management.model.user.UpdateUserRequest;
import com.personalfinance.management.model.user.UserResponse;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(
            path = "/api/users/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody @Valid RegisterUserRequest request){
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        UserResponse response = userService.get(email);

        return WebResponse.<UserResponse>builder()
                .data(response)
                .build();
    }

    @PatchMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody @Valid UpdateUserRequest request){
        String email = userDetails.getUsername();
        UserResponse userResponse = userService.update(email, request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }
}
