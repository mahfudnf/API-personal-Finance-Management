package com.personalfinance.management.service;

import com.personalfinance.management.model.request.CreateSavingRequest;
import com.personalfinance.management.model.request.CreateSavingTransactionRequest;
import com.personalfinance.management.model.request.ListSavingRequest;
import com.personalfinance.management.model.response.SavingProgressResponse;
import com.personalfinance.management.model.response.SavingResponse;
import org.springframework.data.domain.Page;

public interface SavingService {
    SavingResponse createSaving(CreateSavingRequest request);
    SavingResponse getSaving(String savingId);
    void createSavingTransaction(String savingId, CreateSavingTransactionRequest request);
    SavingProgressResponse getSavingProgress(String savingId);
    Page<SavingResponse> listSaving(ListSavingRequest request);
    SavingResponse editSaving(String savingId,CreateSavingRequest request);
    void deleteSaving(String savingId);
}
