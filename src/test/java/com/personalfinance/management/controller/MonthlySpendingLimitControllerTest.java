package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.entity.MonthlySpendingLimit;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.request.CreateSpendingLimitRequest;
import com.personalfinance.management.model.request.UpdateSpendingLimitRequest;
import com.personalfinance.management.model.response.*;
import com.personalfinance.management.repository.MonthlySpendingLimitRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MonthlySpendingLimitControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MonthlySpendingLimitRepository limitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp(){
        limitRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Method Helper
    private String createUserAndGetToken(String email){
        UserEntity user = new UserEntity();
        user.setFirstName("mahfud");
        user.setLastName("nur");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    private MonthlySpendingLimit createSpendingLimit(UserEntity user){
        MonthlySpendingLimit limit = new MonthlySpendingLimit();
        limit.setLimitValue(2_000_000L);
        limit.setMonthValue(MonthValue.MEI);
        limit.setYearValue(2026);
        limit.setUser(user);

        return limitRepository.save(limit);
    }

    @Test
    void createLimitUnauthorized() throws Exception{

        mockMvc.perform(
                post("/api/limits")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void createLimitBadRequest() throws Exception{

        String token = createUserAndGetToken("aaa@gmail.com");

        CreateSpendingLimitRequest request = new CreateSpendingLimitRequest();
        request.setLimitValue(-1L);
        request.setMonthValue(null);
        request.setYearValue(null);

        mockMvc.perform(
                post("/api/limits")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createLimitSuccess() throws Exception{
        String token = createUserAndGetToken("mahfudnf@gmail.com");

        CreateSpendingLimitRequest request = new CreateSpendingLimitRequest();
        request.setLimitValue(2_000_000L);
        request.setMonthValue(MonthValue.MEI);
        request.setYearValue(2026);


        mockMvc.perform(
                post("/api/limits")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<MonthlySpendingLimitResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getLimitId());
            assertNotNull(response.getData().getUserId());
            assertEquals(request.getLimitValue(), response.getData().getLimitValue());
            assertEquals(request.getMonthValue(), response.getData().getMonthValue());
            assertEquals(request.getYearValue(), response.getData().getYearValue());

            assertTrue(limitRepository.existsById(response.getData().getLimitId()));
        });
    }

    @Test
    void getCurrentLimitUnauthorized() throws Exception{

        mockMvc.perform(
                get("/api/limits/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void getCurrentLimitNotFound()throws Exception{
        String token = createUserAndGetToken("bbb@gmail.com");

        mockMvc.perform(
                get("/api/limits/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getCurrentLimitSuccess() throws Exception{
        String token = createUserAndGetToken("ccc@gmail.com");

        UserEntity user = userRepository.findByEmail("ccc@gmail.com").orElseThrow();
        MonthlySpendingLimit limit = createSpendingLimit(user);

        mockMvc.perform(
                get("/api/limits/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<MonthlySpendingLimitResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getLimitId());
            assertNotNull(response.getData().getUserId());
            assertEquals(2_000_000L, response.getData().getLimitValue());
            assertEquals(MonthValue.MEI, response.getData().getMonthValue());
            assertEquals(2026, response.getData().getYearValue());
        });
    }

    @Test
    void getLimitByMonthAndYearUnauthorized() throws Exception{

        mockMvc.perform(
                get("/api/limits/JANUARI/5000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void getLimitByMonthAndYearNotFound()throws Exception{
        String token = createUserAndGetToken("ddd@gmail.com");

        mockMvc.perform(
                get("/api/limits/JANUARI/5000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getLimitByMonthAndYearPathVarMisMatch()throws Exception{
        String token = createUserAndGetToken("eee@gmail.com");

        mockMvc.perform(
                get("/api/limits/SALAH/5000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getLimitByMonthAndYearSuccess() throws Exception{
        String token = createUserAndGetToken("fff@gmail.com");

        UserEntity user = userRepository.findByEmail("fff@gmail.com").orElseThrow();
        MonthlySpendingLimit limit = createSpendingLimit(user);

        mockMvc.perform(
                get("/api/limits/MEI/2026")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<MonthlySpendingLimitResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getLimitId());
            assertNotNull(response.getData().getUserId());
            assertEquals(2_000_000L, response.getData().getLimitValue());
            assertEquals(MonthValue.MEI, response.getData().getMonthValue());
            assertEquals(2026, response.getData().getYearValue());
        });
    }

    @Test
    void listLimitsUnauthorized() throws Exception{

        mockMvc.perform(
                get("/api/limits")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void listLimitsNotFound() throws Exception{
        String token = createUserAndGetToken("ggg@gmail.com");

        mockMvc.perform(
                get("/api/limits")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<MonthlySpendingLimitResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void listLimitsSuccess() throws Exception {
        String token = createUserAndGetToken("hhh@gmail.com");
        UserEntity user = userRepository.findByEmail("hhh@gmail.com").orElseThrow();

        MonthValue[] months = MonthValue.values();

        for (int i = 0; i < 100; i++) {
            MonthlySpendingLimit limit = new MonthlySpendingLimit();
            limit.setLimitValue(2_000_000L);
            limit.setMonthValue(months[i % 12]);
            limit.setYearValue(2020 + (i / 12));
            limit.setUser(user);

            limitRepository.save(limit);
        }

        mockMvc.perform(
                get("/api/limits")
                        .queryParam("limitValue", String.valueOf(2_000_000L))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<MonthlySpendingLimitResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }

    @Test
    void updateLimitUnauthorized() throws Exception{

        mockMvc.perform(
                post("/api/limits/12343554677")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void updateLimitBadRequest() throws Exception{
        String token = createUserAndGetToken("iii@gmail.com");

        UserEntity user = userRepository.findByEmail("iii@gmail.com").orElseThrow();
        MonthlySpendingLimit limit = createSpendingLimit(user);
        String id = limit.getLimitId();

        CreateSpendingLimitRequest request = new CreateSpendingLimitRequest();
        request.setLimitValue(-1L);
        request.setMonthValue(null);
        request.setYearValue(null);

        mockMvc.perform(
                put("/api/limits/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ErrorResponse response = objectMapper
                    .readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateLimitSuccess() throws Exception{
        String token = createUserAndGetToken("jjj@gmail.com");

        UserEntity user = userRepository.findByEmail("jjj@gmail.com").orElseThrow();
        MonthlySpendingLimit limit = createSpendingLimit(user);
        String id = limit.getLimitId();

        UpdateSpendingLimitRequest request = new UpdateSpendingLimitRequest();
        request.setLimitValue(4_000_000L);
        request.setMonthValue(MonthValue.APRIL);
        request.setYearValue(2026);

        mockMvc.perform(
                put("/api/limits/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<MonthlySpendingLimitResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getLimitId());
            assertNotNull(response.getData().getUserId());
            assertEquals(request.getLimitValue(), response.getData().getLimitValue());
            assertEquals(request.getMonthValue(), response.getData().getMonthValue());
            assertEquals(request.getYearValue(), response.getData().getYearValue());

            assertTrue(limitRepository.existsById(response.getData().getLimitId()));
        });
    }


}
