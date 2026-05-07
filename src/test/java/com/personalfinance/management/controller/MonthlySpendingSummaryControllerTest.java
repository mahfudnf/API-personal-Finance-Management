package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.constant.StatusSpent;
import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.MonthlySpendingLimit;
import com.personalfinance.management.entity.MonthlySpendingSummary;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.response.ErrorResponse;
import com.personalfinance.management.model.response.MonthlySpendingSummaryResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.repository.ExpenseRepository;
import com.personalfinance.management.repository.MonthlySpendingLimitRepository;
import com.personalfinance.management.repository.MonthlySpendingSummaryRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.security.service.JwtService;
import com.personalfinance.management.utils.ResponseUtils;
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

import java.util.ArrayList;
import java.util.List;

import static com.personalfinance.management.constant.MonthValue.MEI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MonthlySpendingSummaryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MonthlySpendingLimitRepository limitRepository;

    @Autowired
    private MonthlySpendingSummaryRepository summaryRepository;

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

    @Autowired
    private ResponseUtils responseUtils;

    @BeforeEach
    void setUp(){
        summaryRepository.deleteAll();
        limitRepository.deleteAll();
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

    private MonthlySpendingLimit createSpendingLimit(UserEntity user){
        MonthlySpendingLimit limit = new MonthlySpendingLimit();
        limit.setLimitValue(2_000_000L);
        limit.setMonthValue(MEI);
        limit.setYearValue(2026);
        limit.setUser(user);

        return limitRepository.save(limit);
    }

    private List<Expense> createExpense(UserEntity user){
        List<Expense> expenses = new ArrayList<>();

        for (int i = 0; i < 5; i++){
            Expense expense = new Expense();
            expense.setAmount(50_000L);
            expense.setCategory("makan" + i);
            expense.setDescription("pengeluaran untuk makan");
            expense.setUser(user);
            expenses.add(expenseRepository.save(expense));
        }
        return expenses;
    }

    @Test
    void generateSpendingSummaryNotFound() throws Exception{
        String token = createUserAndGetToken("aaa@gmail.com");
        UserEntity user = userRepository.findByEmail("aaa@gmail.com").orElseThrow();

        mockMvc.perform(
                post("/api/limits/133252272/summaries")
                        .param("monthValue", "6")
                        .param("yearValue", "2026")
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
    void generateSpendingSummaryUnauthorized() throws Exception{

        mockMvc.perform(
                post("/api/limits/133252272/summaries")
                        .param("monthValue", "6")
                        .param("yearValue", "2026")
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
    void generateSpendingSummarySuccess() throws Exception{
        String token = createUserAndGetToken("mahfud@gmail.com");
        UserEntity user = userRepository.findByEmail("mahfud@gmail.com").orElseThrow();
        List<Expense> expenses = createExpense(user);
        MonthlySpendingLimit limit = createSpendingLimit(user);
        String id = limit.getLimitId();

        mockMvc.perform(
                post("/api/limits/"+id+"/summaries")
                        .param("monthValue", "5")
                        .param("yearValue", "2026")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<MonthlySpendingSummaryResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getSummaryId());
            assertNotNull(response.getData().getLimitId());
            assertNotNull(response.getData().getUserId());
            assertEquals(250_000L, response.getData().getTotalSpent());
            assertNotNull(response.getData().getStatusSpent());
            assertEquals(MEI,response.getData().getMonthValue());
            assertEquals(2026,response.getData().getYearValue());
        });
    }

    @Test
    void getCurrentMonthSummaryByUserNotFound() throws Exception{
        String token = createUserAndGetToken("bbb@gmail.com");
        UserEntity user = userRepository.findByEmail("bbb@gmail.com").orElseThrow();

        mockMvc.perform(
                get("/api/summaries/current")
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
    void getCurrentMonthSummaryByUserUnauthorized() throws Exception{

        mockMvc.perform(
                get("/api/summaries/current")
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
    void getCurrentMonthSummaryByUserSuccess() throws Exception{
        String token = createUserAndGetToken("ccc@gmail.com");
        UserEntity user = userRepository.findByEmail("ccc@gmail.com").orElseThrow();
        List<Expense> expenses = createExpense(user);
        MonthlySpendingLimit limit = createSpendingLimit(user);
        Long limitAmount = limit.getLimitValue();

        Integer monthValue = 5;
        Integer yearValue = 2026;

        Long total = expenseRepository.sumByUserIdAndMonthValueAndYearValue(
                user.getUserId(),
                monthValue,
                yearValue
        );

        StatusSpent statusSpent = responseUtils.calculateStatus(total,limitAmount);

        MonthlySpendingSummary summary = new MonthlySpendingSummary();
        summary.setTotalSpent(total);
        summary.setStatusSpent(statusSpent);
        summary.setMonthValue(MonthValue.fromInt(monthValue));
        summary.setYearValue(yearValue);
        summary.setUser(user);
        summary.setMonthlySpendingLimit(limit);
        summaryRepository.save(summary);


        mockMvc.perform(
                get("/api/summaries/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<MonthlySpendingSummaryResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getSummaryId());
            assertNotNull(response.getData().getLimitId());
            assertNotNull(response.getData().getUserId());
            assertEquals(250_000L, response.getData().getTotalSpent());
            assertNotNull(response.getData().getStatusSpent());
            assertEquals(MEI,response.getData().getMonthValue());
            assertEquals(2026,response.getData().getYearValue());
        });
    }

    @Test
    void getSpendingSummaryByUserByMonthAndYearNotFound() throws Exception{
        String token = createUserAndGetToken("ddd@gmail.com");
        UserEntity user = userRepository.findByEmail("ddd@gmail.com").orElseThrow();

        mockMvc.perform(
                get("/api/summaries/12/2026")
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
    void getSpendingSummaryByUserByMonthAndYearUnauthorized() throws Exception{

        mockMvc.perform(
                get("/api/summaries/12/2026")
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
    void getSpendingSummaryByUserByMonthAndYearSuccess() throws Exception{
        String token = createUserAndGetToken("eee@gmail.com");
        UserEntity user = userRepository.findByEmail("eee@gmail.com").orElseThrow();
        List<Expense> expenses = createExpense(user);
        MonthlySpendingLimit limit = createSpendingLimit(user);
        Long limitAmount = limit.getLimitValue();

        Integer monthValue = 5;
        Integer yearValue = 2026;

        Long total = expenseRepository.sumByUserIdAndMonthValueAndYearValue(
                user.getUserId(),
                monthValue,
                yearValue
        );

        StatusSpent statusSpent = responseUtils.calculateStatus(total,limitAmount);

        MonthlySpendingSummary summary = new MonthlySpendingSummary();
        summary.setTotalSpent(total);
        summary.setStatusSpent(statusSpent);
        summary.setMonthValue(MonthValue.fromInt(monthValue));
        summary.setYearValue(yearValue);
        summary.setUser(user);
        summary.setMonthlySpendingLimit(limit);
        summaryRepository.save(summary);


        mockMvc.perform(
                get("/api/summaries/5/2026")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<MonthlySpendingSummaryResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getSummaryId());
            assertNotNull(response.getData().getLimitId());
            assertNotNull(response.getData().getUserId());
            assertEquals(250_000L, response.getData().getTotalSpent());
            assertNotNull(response.getData().getStatusSpent());
            assertEquals(MEI,response.getData().getMonthValue());
            assertEquals(2026,response.getData().getYearValue());
        });
    }

    @Test
    void recalculateSpendingSummaryUnauthorized() throws Exception{

        mockMvc.perform(
                post("/api/limits/12345566776/summaries/recalculate")
                        .param("monthValue", "6")
                        .param("yearValue", "2026")
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
    void recalculateSpendingSummaryNotFound() throws Exception{
        String token = createUserAndGetToken("ggg@gmail.com");
        UserEntity user = userRepository.findByEmail("ggg@gmail.com").orElseThrow();

        mockMvc.perform(
                post("/api/limits/12345566776/summaries/recalculate")
                        .param("monthValue", "6")
                        .param("yearValue", "2026")
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
    void recalculateSpendingSummarySuccess() throws Exception {
        String token = createUserAndGetToken("fff@gmail.com");
        UserEntity user = userRepository.findByEmail("fff@gmail.com").orElseThrow();

        // initial expense (250k)
        createExpense(user);

        MonthlySpendingLimit limit = createSpendingLimit(user);
        String id = limit.getLimitId();

        // generate summary awal
        mockMvc.perform(post("/api/limits/" + id + "/summaries")
                .param("monthValue", "5")
                .param("yearValue", "2026")
                .header("Authorization", "Bearer " + token));

        // tambah expense baru (500k)
        for (int i = 0; i < 5; i++) {
            Expense expense = new Expense();
            expense.setAmount(100_000L);
            expense.setCategory("makan" + i);
            expense.setDescription("pengeluaran tambahan");
            expense.setUser(user);
            expenseRepository.save(expense);
        }

        // recalculate
        mockMvc.perform(
                        post("/api/limits/" + id + "/summaries/recalculate")
                                .param("monthValue", "5")
                                .param("yearValue", "2026")
                                .header("Authorization", "Bearer " + token)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<MonthlySpendingSummaryResponse> response =
                            objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

                    assertEquals(750_000L, response.getData().getTotalSpent()); // ✅ updated
                });
    }

    @AfterEach
    void tearDown() {
        summaryRepository.deleteAll();
        limitRepository.deleteAll();
        expenseRepository.deleteAll();
        userRepository.deleteAll();
    }

}
