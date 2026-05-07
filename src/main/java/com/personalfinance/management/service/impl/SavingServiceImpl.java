package com.personalfinance.management.service.impl;

import com.personalfinance.management.constant.Status;
import com.personalfinance.management.entity.Saving;
import com.personalfinance.management.entity.SavingTransaction;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.exception.custom.ResourceNotFoundException;
import com.personalfinance.management.model.request.CreateSavingRequest;
import com.personalfinance.management.model.request.CreateSavingTransactionRequest;
import com.personalfinance.management.model.request.ListSavingRequest;
import com.personalfinance.management.model.response.SavingProgressResponse;
import com.personalfinance.management.model.response.SavingResponse;
import com.personalfinance.management.repository.SavingRepository;
import com.personalfinance.management.repository.SavingTransactionRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.SavingService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SavingServiceImpl implements SavingService {
    private final UserRepository userRepository;
    private final SavingRepository savingRepository;
    private final SavingTransactionRepository savingTransactionRepository;
    private final ResponseUtils responseUtils;

    public SavingServiceImpl(UserRepository userRepository, SavingRepository savingRepository, SavingTransactionRepository savingTransactionRepository, ResponseUtils responseUtils) {
        this.userRepository = userRepository;
        this.savingRepository = savingRepository;
        this.savingTransactionRepository = savingTransactionRepository;
        this.responseUtils = responseUtils;
    }

    public SavingResponse createSaving(CreateSavingRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Saving saving = new Saving();
        saving.setNameSaving(request.getNameSaving());
        saving.setTargetSaving(request.getTargetSaving());
        saving.setDeadlineSaving(request.getDeadlineSaving());
        saving.setUser(user);
        savingRepository.save(saving);

        return responseUtils.toSavingResponse(saving);
    }

    public SavingResponse getSaving(String savingId){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResourceNotFoundException("Saving dengan id " + savingId + " tidak ditemukan"));

        return responseUtils.toSavingResponse(saving);
    }

    public void createSavingTransaction(String savingId, CreateSavingTransactionRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResourceNotFoundException("Saving dengan id " + savingId + " tidak ditemukan"));

        SavingTransaction transaction = new SavingTransaction();
        transaction.setTransactionAmount(request.getTransactionAmount());
        transaction.setSaving(saving);
        transaction.setUser(user);

        savingTransactionRepository.save(transaction);

        Long currentAmount = savingTransactionRepository.sumBySavingId(savingId);
        saving.setStatus(
                currentAmount >= saving.getTargetSaving()
                        ? Status.SUCCESS
                        : Status.PROGRESS
        );

        savingRepository.save(saving);
    }

    public SavingProgressResponse getSavingProgress(String savingId){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResourceNotFoundException("Saving dengan id " + savingId + " tidak ditemukan"));


        return responseUtils.toSavingProgressResponse(saving);
    }

    public Page<SavingResponse> listSaving(ListSavingRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Saving> savings;
        if (request.getNameSaving() != null) {
            savings = savingRepository.findByUserAndNameSavingContaining(
                    user,
                    request.getNameSaving(),
                    pageable
            );
        } else {
            savings = savingRepository.findByUser(user, pageable);
        }

        return savings.map(responseUtils::toSavingResponse);
    }

    public SavingResponse editSaving(String savingId, CreateSavingRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResourceNotFoundException("Saving dengan id " + savingId + " tidak ditemukan"));

        saving.setNameSaving(request.getNameSaving());
        saving.setTargetSaving(request.getTargetSaving());
        saving.setDeadlineSaving(request.getDeadlineSaving());
        savingRepository.save(saving);

        return responseUtils.toSavingResponse(saving);
    }

    public void deleteSaving(String savingId){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResourceNotFoundException("Saving dengan id " + savingId + " tidak ditemukan"));

        savingRepository.delete(saving);
    }


}
