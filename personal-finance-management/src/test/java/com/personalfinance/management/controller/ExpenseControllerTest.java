package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.expense.CreateExpenseRequest;
import com.personalfinance.management.model.expense.ExpenseResponse;
import com.personalfinance.management.model.expense.UpdateExpenseRequest;
import com.personalfinance.management.model.income.CreateIncomeRequest;
import com.personalfinance.management.repository.ExpenseRepository;
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
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        expenseRepository.deleteAll();
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

    private Expense createExpense(UserEntity user){
        Expense expense = new Expense();
        expense.setAmount(50000L);
        expense.setCategory("makan");
        expense.setDescription("pengeluaran untuk makan");
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @Test
    void createExpenseBadRequest() throws Exception{
        String token = createUserAndGetToken();

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
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createExpenseSuccess()throws Exception{
        String token = createUserAndGetToken();

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setAmount(50000L);
        request.setCategory("makan");
        request.setDescription("pengeluaran untuk makan");

        mockMvc.perform(
                post("/api/expenses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ExpenseResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getExpenseId());
            assertEquals(request.getAmount(),response.getData().getAmount());
            assertEquals(request.getCategory(),response.getData().getCategory());
            assertNotNull(response.getData().getCreatedAt());
            assertEquals(request.getDescription(),response.getData().getDescription());

            assertTrue(expenseRepository.existsById(response.getData().getExpenseId()));
        });
    }

    @Test
    void getExpenseNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                get("/api/expenses/23344566777")
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
    void getExpenseSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
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
            assertNull(response.getErrors());
            assertNotNull(response.getData().getExpenseId());
            assertEquals(50000L,response.getData().getAmount());
            assertEquals("makan",response.getData().getCategory());
            assertNotNull(response.getData().getCreatedAt());
            assertEquals("pengeluaran untuk makan",response.getData().getDescription());
        });
    }

    @Test
    void listExpenseNotFound() throws Exception{
        String token = createUserAndGetToken();

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
            assertNull(response.getErrors());
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void listExpenseSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();

        for (int i=0 ; i<100 ;i++){
            Expense expense = new Expense();
            expense.setAmount(50000L);
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
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void editExpenseBadRequest() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
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
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void editExpenseSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Expense expense = createExpense(user);
        String id = expense.getExpenseId();

        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setAmount(20000L);
        request.setCategory("rokok");
        request.setDescription("pengeluaran untuk rokok");

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
            assertNull(response.getErrors());
            assertNotNull(response.getData().getExpenseId());
            assertEquals(request.getAmount(),response.getData().getAmount());
            assertEquals(request.getCategory(),response.getData().getCategory());
            assertNotNull(response.getData().getCreatedAt());
            assertEquals(request.getDescription(),response.getData().getDescription());

            assertTrue(expenseRepository.existsById(response.getData().getExpenseId()));
        });
    }

    @Test
    void deleteExpenseNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                delete("/api/expenses/23445566")
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
    void deleteExpenseSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
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
            assertNull(response.getErrors());
            assertEquals("OK",response.getData());
        });
    }
}
