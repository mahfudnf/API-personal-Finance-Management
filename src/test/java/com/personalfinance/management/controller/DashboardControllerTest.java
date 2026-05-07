package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.entity.*;
import com.personalfinance.management.model.response.ErrorResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.model.response.DashboardResponse;
import com.personalfinance.management.repository.*;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
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
    private MonthlySpendingLimitRepository limitRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        expenseRepository.deleteAll();
        incomeRepository.deleteAll();
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

    private Income createIncome(UserEntity user){
        Income income = new Income();
        income.setAmount(1_000_000L);
        income.setCategory("gaji");
        income.setDescription("gaji bulan april");
        income.setUser(user);

        return incomeRepository.save(income);
    }

    private Saving createSaving(UserEntity user){
        Saving saving = new Saving();
        saving.setNameSaving("tabungan umroh");
        saving.setTargetSaving(50_000_000L);
        saving.setDeadlineSaving(LocalDate.parse("2026-12-01"));
        saving.setUser(user);

        return savingRepository.save(saving);
    }

    private SavingTransaction createSavingTransaction(Saving saving,UserEntity user){
        SavingTransaction transaction = new SavingTransaction();
        transaction.setTransactionAmount(10_000_000L);
        transaction.setSaving(saving);
        transaction.setUser(user);

        return savingTransactionRepository.save(transaction);
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
    void getDashboardUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/dashboard")
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
    void getDashboardEmptyData() throws Exception {
        String token = createUserAndGetToken("aaa@gmail.com");

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
            assertEquals(0L,response.getData().getSpendingLimit());
        });
    }

    @Test
    void getDashboardSuccess() throws Exception{
        String token = createUserAndGetToken("mahfud@gmail.com");

        UserEntity user = userRepository.findByEmail("mahfud@gmail.com").orElseThrow();
        Expense expense = createExpense(user);
        Income income = createIncome(user);
        MonthlySpendingLimit limit = createSpendingLimit(user);
        Saving saving = createSaving(user);
        SavingTransaction savingTransaction = createSavingTransaction(saving,user);

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
            assertEquals(1_000_000L,response.getData().getTotalIncome());
            assertEquals(50_000L,response.getData().getTotalExpense());
            assertEquals(950_000L,response.getData().getCurrentBalance());
            assertEquals(2_000_000L,response.getData().getSpendingLimit());
            assertNotNull(response.getData().getSavingProgress());
        });
    }


}
