package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.Saving;
import com.personalfinance.management.entity.SavingTransaction;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.expense.CreateExpenseRequest;
import com.personalfinance.management.model.income.IncomeResponse;
import com.personalfinance.management.model.saving.CreateSavingRequest;
import com.personalfinance.management.model.saving.CreateSavingTransactionRequest;
import com.personalfinance.management.model.saving.SavingProgressResponse;
import com.personalfinance.management.model.saving.SavingResponse;
import com.personalfinance.management.repository.SavingRepository;
import com.personalfinance.management.repository.SavingTransactionRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SavingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private SavingTransactionRepository savingTransactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        savingRepository.deleteAll();
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
    void createSavingBadRequest() throws Exception{
        String token = createUserAndGetToken();

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving(null);
        request.setTargetAmount(null);

        mockMvc.perform(
                post("/api/savings")
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
    void createSavingSuccess() throws Exception{
        String token = createUserAndGetToken();

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving("tabungan umroh");
        request.setTargetAmount(50000000L);
        request.setDeadline(LocalDate.parse("2026-12-01"));

        mockMvc.perform(
                post("/api/savings")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<SavingResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getSavingId());
            assertEquals(request.getNameSaving(),response.getData().getNameSaving());
            assertEquals(request.getTargetAmount(),response.getData().getTargetAmount());
            assertEquals(request.getDeadline(),response.getData().getDeadline());
            assertNotNull(response.getData().getCreatedAt());

            assertTrue(savingRepository.existsById(response.getData().getSavingId()));
        });
    }

    @Test
    void getSavingNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                get("/api/savings/244551666177")
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
    void getSavingSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        mockMvc.perform(
                get("/api/savings/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<SavingResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getSavingId());
            assertEquals("tabungan umroh",response.getData().getNameSaving());
            assertEquals(50000000L,response.getData().getTargetAmount());
            assertNotNull(response.getData().getDeadline());
            assertNotNull(response.getData().getCreatedAt());
        });
    }

    @Test
    void createSavingTransactionBadRequest() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingTransactionRequest request = new CreateSavingTransactionRequest();
        request.setAmount(null);

        mockMvc.perform(
                post("/api/savings/"+id+"/saving_transaction")
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
    void createSavingTransactionSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingTransactionRequest request = new CreateSavingTransactionRequest();
        request.setAmount(10000000L);

        mockMvc.perform(
                post("/api/savings/"+id+"/saving_transaction")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
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

    @Test
    void getSavingProgressNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                get("/api/savings/244551666177/saving_transaction/progress")
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
    void getSavingProgressSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        SavingTransaction transaction1 = createSavingTransaction(saving);
        SavingTransaction transaction2 = createSavingTransaction(saving);


        mockMvc.perform(
                get("/api/savings/"+id+"/saving_transaction/progress")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<SavingProgressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getSavingId());
            assertEquals("tabungan umroh",response.getData().getNameSaving());
            assertEquals(50000000L,response.getData().getTargetAmount());
            assertEquals(20000000L,response.getData().getCurrentBalance());
            assertEquals(30000000L,response.getData().getRemainingAmount());
            assertEquals(40,response.getData().getProgressPercentage());
        });
    }

    @Test
    void listSavingNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                get("/api/savings")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SavingResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void listSavingSuccess() throws Exception{
        String token = createUserAndGetToken();
        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();

        for (int i=0 ; i<100 ; i++){
            Saving saving = new Saving();
            saving.setNameSaving("tabungan haji" + i);
            saving.setTargetAmount(50000000L);
            saving.setDeadline(LocalDate.parse("2026-12-01"));
            saving.setCreatedAt(LocalDateTime.now());
            saving.setUser(user);
            savingRepository.save(saving);
        }

        mockMvc.perform(
                get("/api/savings")
                        .queryParam("nameSaving","tabungan haji")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SavingResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void editSavingBadRequest() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving(null);
        request.setTargetAmount(null);
        request.setDeadline(LocalDate.parse("2026-12-01"));

        mockMvc.perform(
                put("/api/savings/" + id)
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
    void editSavingSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving("beli motor");
        request.setTargetAmount(30000000L);
        request.setDeadline(LocalDate.parse("2026-12-01"));

        mockMvc.perform(
                put("/api/savings/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<SavingResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getSavingId());
            assertEquals(request.getNameSaving(),response.getData().getNameSaving());
            assertEquals(request.getTargetAmount(),response.getData().getTargetAmount());
            assertEquals(request.getDeadline(),response.getData().getDeadline());
            assertNotNull(response.getData().getCreatedAt());

            assertTrue(savingRepository.existsById(response.getData().getSavingId()));
        });
    }

    @Test
    void deleteSavingNotFound() throws Exception{
        String token = createUserAndGetToken();

        mockMvc.perform(
                delete("/api/savings/15527729828")
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
    void deleteSavingSuccess() throws Exception{
        String token = createUserAndGetToken();

        UserEntity user = userRepository.findByEmail("joko@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        mockMvc.perform(
                delete("/api/savings/" + id)
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
