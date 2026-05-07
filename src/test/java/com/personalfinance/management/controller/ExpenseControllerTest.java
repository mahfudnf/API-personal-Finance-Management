package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.response.ErrorResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.model.request.CreateExpenseRequest;
import com.personalfinance.management.model.response.ExpenseResponse;
import com.personalfinance.management.model.request.UpdateExpenseRequest;
import com.personalfinance.management.repository.ExpenseRepository;
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
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

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
        expenseRepository.deleteAll();
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

    private Expense createExpense(UserEntity user){
        Expense expense = new Expense();
        expense.setAmount(50_000L);
        expense.setCategory("makan");
        expense.setDescription("pengeluaran untuk makan");
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @Test
    void createExpenseBadRequest() throws Exception{
        String token = createUserAndGetToken("aaa@gmail.com");

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setAmount(null);
        request.setCategory("");
        request.setDescription("");

        mockMvc.perform(
                post("/api/expenses")
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
    void createExpenseSuccess()throws Exception{
        String token = createUserAndGetToken("mahfud@gmail.com");

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setAmount(50_000L);
        request.setCategory("makan");
        request.setDescription("pengeluaran untuk makan");

        mockMvc.perform(
                post("/api/expenses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getExpenseId());
            assertNotNull(response.getData().getUserId());
            assertEquals(request.getAmount(), response.getData().getAmount());
            assertEquals(request.getCategory(), response.getData().getCategory());
            assertEquals(request.getDescription(), response.getData().getDescription());
            assertNotNull(response.getData().getCreatedAt());
            assertNotNull(response.getData().getUpdatedAt());

            assertTrue(expenseRepository.existsById(response.getData().getExpenseId()));
        });
    }

    @Test
    void getExpenseNotFound() throws Exception{
        String token = createUserAndGetToken("bbb@gmail.com");

        mockMvc.perform(
                get("/api/expenses/23344566777")
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
    void getExpenseSuccess() throws Exception{
        String token = createUserAndGetToken("ccc@gmail.com");

        UserEntity user = userRepository.findByEmail("ccc@gmail.com").orElseThrow();
        Expense expense = createExpense(user);
        String id = expense.getExpenseId();

        mockMvc.perform(
                get("/api/expenses/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getExpenseId());
            assertNotNull(response.getData().getUserId());
            assertEquals(50_000L,response.getData().getAmount());
            assertEquals("makan",response.getData().getCategory());
            assertEquals("pengeluaran untuk makan",response.getData().getDescription());
            assertNotNull(response.getData().getCreatedAt());
            assertNotNull(response.getData().getUpdatedAt());
        });
    }

    @Test
    void listExpenseNotFound() throws Exception{
        String token = createUserAndGetToken("ddd@gmail.com");

        mockMvc.perform(
                get("/api/expenses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ExpenseResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void listExpenseSuccess() throws Exception{
        String token = createUserAndGetToken("ddd@gmail.com");

        UserEntity user = userRepository.findByEmail("ddd@gmail.com").orElseThrow();

        for (int i=0 ; i<100 ;i++){
            Expense expense = new Expense();
            expense.setAmount(50_000L);
            expense.setCategory("makan" + i);
            expense.setDescription("pengeluaran untuk makan");
            expense.setUser(user);
            expenseRepository.save(expense);
        }

        mockMvc.perform(
                get("/api/expenses")
                        .queryParam("category","makan")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ExpenseResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void editExpenseBadRequest() throws Exception{
        String token = createUserAndGetToken("eee@gmail.com");

        UserEntity user = userRepository.findByEmail("eee@gmail.com").orElseThrow();
        Expense expense = createExpense(user);
        String id = expense.getExpenseId();

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setAmount(-1L);

        mockMvc.perform(
                patch("/api/expenses/" + id)
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
    void editExpenseSuccess() throws Exception{
        String token = createUserAndGetToken("fff@gmail.com");

        UserEntity user = userRepository.findByEmail("fff@gmail.com").orElseThrow();
        Expense expense = createExpense(user);
        String id = expense.getExpenseId();

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setAmount(20_000L);
        request.setCategory("transport");
        request.setDescription("pengeluaran untuk transport");

        mockMvc.perform(
                patch("/api/expenses/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getExpenseId());
            assertNotNull(response.getData().getUserId());
            assertEquals(request.getAmount(), response.getData().getAmount());
            assertEquals(request.getCategory(), response.getData().getCategory());
            assertEquals(request.getDescription(), response.getData().getDescription());
            assertNotNull(response.getData().getCreatedAt());
            assertNotNull(response.getData().getUpdatedAt());

            assertTrue(expenseRepository.existsById(response.getData().getExpenseId()));
        });
    }

    @Test
    void deleteExpenseNotFound() throws Exception{
        String token = createUserAndGetToken("ggg@gmail.com");

        mockMvc.perform(
                delete("/api/expenses/23445566")
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
    void deleteExpenseSuccess() throws Exception{
        String token = createUserAndGetToken("hhh@gmail.com");

        UserEntity user = userRepository.findByEmail("hhh@gmail.com").orElseThrow();
        Expense expense = createExpense(user);
        String id = expense.getExpenseId();

        mockMvc.perform(
                delete("/api/expenses/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("OK",response.getData());
        });
    }
}
