package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.request.LoginUserRequest;
import com.personalfinance.management.model.response.ErrorResponse;
import com.personalfinance.management.model.response.TokenResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.security.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailed() throws Exception{
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("aaa@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("salah@gmail.com");
        request.setPassword("salah");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getMessage());
        });
    }

    @Test
    void loginFailedWrongPassword() throws Exception{
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("bbb@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("mahfud@gmail.com");
        request.setPassword("salah");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getMessage());
        });
    }

    @Test
    void loginSuccess() throws Exception{
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("mahfud@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("mahfud@gmail.com");
        request.setPassword("abc123");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());
            UserEntity userDb = userRepository.findByEmail("mahfud@gmail.com").orElse(null);
            assertNotNull(userDb);
        });
    }

    @Test
    void logoutSuccess() throws Exception {
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("ccc@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                post("/api/users/logout")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(
                        status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });
                    assertEquals("OK", response.getData());
                });
    }

}
