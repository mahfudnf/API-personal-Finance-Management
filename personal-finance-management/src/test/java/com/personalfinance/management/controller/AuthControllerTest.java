package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.user.LoginUserRequest;
import com.personalfinance.management.model.TokenResponse;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
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
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void loginFailed() throws Exception{

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("salah");
        request.setPassword("salah");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginFailedWrongPassword() throws Exception{
        UserEntity user = new UserEntity();
        user.setName("joko");
        user.setEmail("joko@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("joko");
        request.setPassword("salah");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginSuccess() throws Exception{
        UserEntity user = new UserEntity();
        user.setName("joko");
        user.setEmail("joko@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("joko@gmail.com");
        request.setPassword("abc123");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            UserEntity userDb = userRepository.findByEmail("joko@gmail.com").orElse(null);
            assertNotNull(userDb);
        });
    }

    @Test
    void logoutFailed() throws Exception {
        mockMvc.perform(
                post("/api/users/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void logoutSuccess() throws Exception {
        UserEntity user = new UserEntity();
        user.setName("joko");
        user.setEmail("joko@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        String token = jwtUtil.generateToken("joko@gmail.com");

        mockMvc.perform(
                post("/api/users/logout")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });
                    assertEquals("OK", response.getData());
                });
    }

}
