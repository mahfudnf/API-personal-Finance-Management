package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.request.RegisterUserRequest;
import com.personalfinance.management.model.request.UpdateUserRequest;
import com.personalfinance.management.model.response.ErrorResponse;
import com.personalfinance.management.model.response.UserResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {
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
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception{
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFirstName("mahfud");
        request.setLastName("nur");
        request.setEmail("mahfud@gmail.com");
        request.setPassword("abc123");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("OK",response.getData());
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception{
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterDuplicated() throws Exception{
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("mahfud@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setFirstName("mahfud");
        request.setLastName("nur");
        request.setEmail("mahfud@gmail.com");
        request.setPassword("abc123");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorized() throws Exception{

        mockMvc.perform(
                post("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + "not found")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorizedTokenNotSend() throws Exception{
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception{
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("mahfud@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("mahfud", response.getData().getFirstName());
            assertEquals("nur", response.getData().getLastName());
            assertEquals("mahfud@gmail.com",response.getData().getEmail());
        });
    }

    @Test
    void getUserTokenExpired() throws Exception{
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("aaa@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        String expiredToken = jwtService.testGenerateExpiredToken(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + expiredToken)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserUnauthorized() throws Exception{
        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserSuccess()throws Exception{
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail("bbb@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("budi");
        request.setLastName("tabuti");
        request.setPassword("xyz123");

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("budi",response.getData().getFirstName());
            assertEquals("tabuti",response.getData().getLastName());
        });

        UserEntity userDb = userRepository.findByEmail("bbb@gmail.com").orElse(null);
        assertNotNull(userDb);
        System.out.println(userDb.getPassword());
        assertTrue(passwordEncoder.matches("xyz123",userDb.getPassword()));
    }

}
