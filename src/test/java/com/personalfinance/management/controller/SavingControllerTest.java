package com.personalfinance.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalfinance.management.entity.Saving;
import com.personalfinance.management.entity.SavingTransaction;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.response.ErrorResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.model.request.CreateSavingRequest;
import com.personalfinance.management.model.request.CreateSavingTransactionRequest;
import com.personalfinance.management.model.response.SavingProgressResponse;
import com.personalfinance.management.model.response.SavingResponse;
import com.personalfinance.management.repository.SavingRepository;
import com.personalfinance.management.repository.SavingTransactionRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
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
    private JwtService jwtService;

    @BeforeEach
    void setUp(){
        savingRepository.deleteAll();
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

    @Test
    void createSavingBadRequest() throws Exception{
        String token = createUserAndGetToken("aaa@gmail.com");

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving(null);
        request.setTargetSaving(null);

        mockMvc.perform(
                post("/api/savings")
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
    void createSavingSuccess() throws Exception{
        String token = createUserAndGetToken("mahfud@gmail.com");

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving("tabungan umroh");
        request.setTargetSaving(50_000_000L);
        request.setDeadlineSaving(LocalDate.parse("2026-12-01"));

        mockMvc.perform(
                post("/api/savings")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<SavingResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData().getSavingId());
            assertNotNull(response.getData().getUserId());
            assertEquals(request.getNameSaving(),response.getData().getNameSaving());
            assertEquals(request.getTargetSaving(),response.getData().getTargetSaving());
            assertEquals(request.getDeadlineSaving(),response.getData().getDeadlineSaving());
            assertNotNull(response.getData().getCurrentAmount());
            assertNotNull(response.getData().getStatus());
            assertNotNull(response.getData().getCreatedAt());
            assertNotNull(response.getData().getUpdatedAt());

            assertTrue(savingRepository.existsById(response.getData().getSavingId()));
        });
    }

    @Test
    void getSavingNotFound() throws Exception{
        String token = createUserAndGetToken("bbb@gmail.com");

        mockMvc.perform(
                get("/api/savings/244551666177")
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
    void getSavingSuccess() throws Exception{
        String token = createUserAndGetToken("ccc@gmail.com");

        UserEntity user = userRepository.findByEmail("ccc@gmail.com").orElseThrow();
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
            assertNotNull(response.getData().getSavingId());
            assertNotNull(response.getData().getUserId());
            assertEquals("tabungan umroh",response.getData().getNameSaving());
            assertEquals(50_000_000L,response.getData().getTargetSaving());
            assertNotNull(response.getData().getDeadlineSaving());
            assertNotNull(response.getData().getCurrentAmount());
            assertNotNull(response.getData().getStatus());
            assertNotNull(response.getData().getCreatedAt());
            assertNotNull(response.getData().getUpdatedAt());
        });
    }

    @Test
    void createSavingTransactionBadRequest() throws Exception{
        String token = createUserAndGetToken("ddd@gmail.com");

        UserEntity user = userRepository.findByEmail("ddd@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingTransactionRequest request = new CreateSavingTransactionRequest();
        request.setTransactionAmount(null);

        mockMvc.perform(
                post("/api/savings/"+id+"/saving_transaction")
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
    void createSavingTransactionSuccess() throws Exception{
        String token = createUserAndGetToken("eee@gmail.com");

        UserEntity user = userRepository.findByEmail("eee@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingTransactionRequest request = new CreateSavingTransactionRequest();
        request.setTransactionAmount(10_000_000L);

        mockMvc.perform(
                post("/api/savings/"+id+"/saving_transaction")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("OK",response.getData());
        });
    }

    @Test
    void getSavingProgressNotFound() throws Exception{
        String token = createUserAndGetToken("fff@gmail.com");

        mockMvc.perform(
                get("/api/savings/244551666177/saving_transaction/progress")
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
    void getSavingProgressSuccess() throws Exception{
        String token = createUserAndGetToken("ggg@gmail.com");

        UserEntity user = userRepository.findByEmail("ggg@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        SavingTransaction transaction1 = createSavingTransaction(saving,user);
        SavingTransaction transaction2 = createSavingTransaction(saving,user);


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
            assertNotNull(response.getData().getSavingId());
            assertNotNull(response.getData().getUserId());
            assertEquals("tabungan umroh",response.getData().getNameSaving());
            assertEquals(50_000_000L,response.getData().getTargetSaving());
            assertEquals(20_000_000L,response.getData().getCurrentAmount());
            assertNotNull(response.getData().getStatus());
            assertEquals(40,response.getData().getProgressPercentage());
            assertEquals(30_000_000L,response.getData().getRemainingAmount());
        });
    }

    @Test
    void listSavingNotFound() throws Exception{
        String token = createUserAndGetToken("hhh@gmail.com");

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
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(10,response.getPaging().getSize());
        });
    }

    @Test
    void listSavingSuccess() throws Exception{
        String token = createUserAndGetToken("iii@gmail.com");
        UserEntity user = userRepository.findByEmail("iii@gmail.com").orElseThrow();

        for (int i=0 ; i<100 ; i++){
            Saving saving = new Saving();
            saving.setNameSaving("tabungan haji" + i);
            saving.setTargetSaving(50_000_000L);
            saving.setDeadlineSaving(LocalDate.parse("2026-12-01"));
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
        String token = createUserAndGetToken("jjj@gmail.com");

        UserEntity user = userRepository.findByEmail("jjj@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving(null);
        request.setTargetSaving(null);
        request.setDeadlineSaving(LocalDate.parse("2026-12-01"));

        mockMvc.perform(
                put("/api/savings/" + id)
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
    void editSavingSuccess() throws Exception{
        String token = createUserAndGetToken("kkk@gmail.com");

        UserEntity user = userRepository.findByEmail("kkk@gmail.com").orElseThrow();
        Saving saving = createSaving(user);
        String id = saving.getSavingId();

        CreateSavingRequest request = new CreateSavingRequest();
        request.setNameSaving("beli motor");
        request.setTargetSaving(30_000_000L);
        request.setDeadlineSaving(LocalDate.parse("2026-12-01"));

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
            assertNotNull(response.getData().getSavingId());
            assertNotNull(response.getData().getUserId());
            assertEquals(request.getNameSaving(),response.getData().getNameSaving());
            assertEquals(request.getTargetSaving(),response.getData().getTargetSaving());
            assertEquals(request.getDeadlineSaving(),response.getData().getDeadlineSaving());
            assertNotNull(response.getData().getCurrentAmount());
            assertNotNull(response.getData().getStatus());
            assertNotNull(response.getData().getCreatedAt());
            assertNotNull(response.getData().getUpdatedAt());

            assertTrue(savingRepository.existsById(response.getData().getSavingId()));
        });
    }

    @Test
    void deleteSavingNotFound() throws Exception{
        String token = createUserAndGetToken("lll@gmail.com");

        mockMvc.perform(
                delete("/api/savings/15527729828")
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
    void deleteSavingSuccess() throws Exception{
        String token = createUserAndGetToken("mmm@gmail.com");

        UserEntity user = userRepository.findByEmail("mmm@gmail.com").orElseThrow();
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
            assertEquals("OK",response.getData());
        });
    }

}
