package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.Income;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.income.CreateIncomeRequest;
import com.personalfinance.management.model.income.IncomeResponse;
import com.personalfinance.management.model.income.UpdateIncomeRequest;
import com.personalfinance.management.repository.IncomeRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        incomeRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Method Helper
    private String createUserAndGetToken(){
        UserEntity user = new UserEntity();
        user.setName("joko");
        user.setEmail("joko@gmail.com");
        user.setPassword(passwordEncoder.encode("abc123"));
        userRepository.save(user);

        return jwtUtil.generateToken(user.getEmail());
    }

    private Income createIncome(UserEntity user){
        Income income = new Income();
        income.setAmount(100000L);
        income.setCategory("gaji");
        income.setDescription("gaji bulan april");
        income.setUser(user);

        return incomeRepository.save(income);
    }

    @Test
    void createIncomeBadRequest() throws Exception{

        String token = createUserAndGetToken();

        CreateIncomeRequest request = new CreateIncomeRequest();
        request.setAmount(null);
        request.setCategory("");
        request.setDescription("");

        mockMvc.perform(
                post("/api/incomes")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createIncomeSuccess() throws Exception{
        String token = createUserAndGetToken();

        CreateIncomeRequest request = new CreateIncomeRequest();
        request.setAmount(100000L);
        request.setCategory("gaji");
        request.setDescription("gaji bulan april");

        mockMvc.perform(
                post("/api/incomes")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<IncomeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getIncomeId());
            assertEquals(request.getAmount(), response.getData().getAmount());
            assertEquals(request.getCategory(), response.getData().getCategory());
            assertNotNull(response.getData().getCreatedAt());
            assertEquals(request.getDescription(), response.getData().getDescription());

            assertTrue(incomeRepository.existsById(response.getData().getIncomeId()));
        });
    }

    @Test
    void getIncomeNotFound()throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                get("/api/incomes/23344566777")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getIncomeSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Income income = createIncome(user);
        String id = income.getIncomeId();

        mockMvc.perform(
                get("/api/incomes/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<IncomeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getIncomeId());
            assertEquals(100000L, response.getData().getAmount());
            assertEquals("gaji", response.getData().getCategory());
            assertNotNull(response.getData().getCreatedAt());
            assertEquals("gaji bulan april", response.getData().getDescription());
        });
    }

    @Test
    void listIncomeNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                get("/api/incomes")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<IncomeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void listIncomeSuccess() throws Exception{
        String token = createUserAndGetToken();
        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();

        for (int i=0 ; i<100 ; i++){
            Income income = new Income();
            income.setAmount(200000L);
            income.setCategory("freelance" + i);
            income.setDescription("gaji dari freelance");
            income.setUser(user);
            incomeRepository.save(income);
        }

        mockMvc.perform(
                get("/api/incomes")
                        .queryParam("category","freelance")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<IncomeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void editIncomeBadRequest() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Income income = createIncome(user);
        String id = income.getIncomeId();

        UpdateIncomeRequest request = new UpdateIncomeRequest();
        request.setAmount(-1L);

        mockMvc.perform(
                patch("/api/incomes/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void editIncomeSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Income income = createIncome(user);
        String id = income.getIncomeId();

        UpdateIncomeRequest request = new UpdateIncomeRequest();
        request.setAmount(200000L);
        request.setCategory("freelance");
        request.setDescription("gaji dari freelance");

        mockMvc.perform(
                patch("/api/incomes/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<IncomeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getIncomeId());
            assertEquals(request.getAmount(),response.getData().getAmount());
            assertEquals(request.getCategory(),response.getData().getCategory());
            assertNotNull(response.getData().getCreatedAt());
            assertEquals(request.getDescription(),response.getData().getDescription());

            assertTrue(incomeRepository.existsById(response.getData().getIncomeId()));
        });
    }

    @Test
    void deleteIncomeNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                delete("/api/incomes/2345667777")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteIncomeSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Income income = createIncome(user);
        String id = income.getIncomeId();

        mockMvc.perform(
                delete("/api/incomes/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("OK",response.getData());
        });
    }
}
