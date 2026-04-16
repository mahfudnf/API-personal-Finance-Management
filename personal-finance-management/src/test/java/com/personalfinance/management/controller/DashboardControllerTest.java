package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.*;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.dashboard.DashboardResponse;
import com.personalfinance.management.model.saving.SavingProgressResponse;
import com.personalfinance.management.repository.*;
import com.personalfinance.management.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private SavingTransactionRepository savingTransactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        expenseRepository.deleteAll();
        incomeRepository.deleteAll();
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

    private Income createIncome(UserEntity user){
        Income income = new Income();
        income.setAmount(100000L);
        income.setCategory("gaji");
        income.setDescription("gaji bulan april");
        income.setUser(user);

        return incomeRepository.save(income);
    }

    private Saving createSaving(UserEntity user){
        Saving saving = new Saving();
        saving.setNameSaving("tabungan umroh");
        saving.setTargetAmount(50000000L);
        saving.setDeadline(LocalDate.parse("2026-12-01"));
        saving.setCreatedAt(LocalDateTime.now());
        saving.setUser(user);

        return savingRepository.save(saving);
    }

    private SavingTransaction createSavingTransaction(Saving saving){
        SavingTransaction transaction = new SavingTransaction();
        transaction.setAmount(10000000L);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSaving(saving);

        return savingTransactionRepository.save(transaction);
    }


    @Test
    void getDashboardSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Expense expense = createExpense(user);
        Income income = createIncome(user);

        mockMvc.perform(
                get("/api/dashboard")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DashboardResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(100000L,response.getData().getTotalIncome());
            assertEquals(50000L,response.getData().getTotalExpense());
            assertEquals(50000L,response.getData().getCurrentBalance());
        });
    }

    @Test
    void getDashboardEmptyData() throws Exception {
        String token = createUserAndGetToken();

        mockMvc.perform(
                        get("/api/dashboard")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                ).andExpect(status().isOk())
                .andDo(result -> {

                    WebResponse<DashboardResponse> response =
                            objectMapper.readValue(
                                    result.getResponse().getContentAsString(),
                                    new TypeReference<>() {}
                            );

                    assertEquals(0L, response.getData().getTotalIncome());
                    assertEquals(0L, response.getData().getTotalExpense());
                    assertEquals(0L, response.getData().getCurrentBalance());
                });
    }

    @Test
    void getDashboardUnauthorized() throws Exception {

        mockMvc.perform(
                get("/api/dashboard")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void getUserSavingProgressEmptyData() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);

        mockMvc.perform(
                        get("/api/dashboard/savings/saving_transaction/progress")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                ).andExpect(status().isOk())
                .andDo(result -> {
            WebResponse<List<SavingProgressResponse>> response =
                    objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<List<SavingProgressResponse>>>() {}
                    );

            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertEquals(1, response.getData().size());

            SavingProgressResponse progress = response.getData().get(0);

            assertEquals(0L, progress.getCurrentBalance());
            assertEquals(progress.getTargetAmount(), progress.getRemainingAmount());
            assertEquals(0.0, progress.getProgressPercentage());
        });
    }

    @Test
    void getUserSavingProgressSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        SavingTransaction transaction = createSavingTransaction(saving);

        mockMvc.perform(
                        get("/api/dashboard/savings/saving_transaction/progress")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<List<SavingProgressResponse>> response =
                            objectMapper.readValue(
                                    result.getResponse().getContentAsString(),
                                    new TypeReference<WebResponse<List<SavingProgressResponse>>>() {
                                    }
                            );

                    assertNull(response.getErrors());
                    assertNotNull(response.getData());
                    assertEquals(1, response.getData().size());

                    SavingProgressResponse progress = response.getData().get(0);

                    assertEquals(10000000L, progress.getCurrentBalance());
                    assertEquals(40000000L, progress.getRemainingAmount());
                    assertEquals(20.0, progress.getProgressPercentage());
                });
    }
}
