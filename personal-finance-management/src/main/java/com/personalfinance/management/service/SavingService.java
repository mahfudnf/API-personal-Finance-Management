package com.personalfinance.management.service;

import com.personalfinance.management.model.saving.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface SavingService {

    SavingResponse createSaving(String email, @Valid CreateSavingRequest request);
    SavingResponse getSaving(String email,String savingId);
    void createSavingTransaction(String email, String savingId,@Valid CreateSavingTransactionRequest request);
    SavingProgressResponse getSavingProgress(String email, String savingId);
    Page<SavingResponse> listSaving(String email, @Valid ListSavingRequest request);
    SavingResponse editSaving(String email,String savingId,@Valid CreateSavingRequest request);
    void deleteSaving(String email,String savingId);
}
