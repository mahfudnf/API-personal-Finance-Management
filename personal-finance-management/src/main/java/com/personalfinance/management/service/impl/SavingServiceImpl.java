package com.personalfinance.management.service.impl;

import com.personalfinance.management.entity.Saving;
import com.personalfinance.management.entity.SavingTransaction;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.saving.*;
import com.personalfinance.management.repository.SavingRepository;
import com.personalfinance.management.repository.SavingTransactionRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.SavingService;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class SavingServiceImpl implements SavingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private SavingTransactionRepository savingTransactionRepository;

    @Transactional
    public SavingResponse createSaving(String email,@Valid CreateSavingRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Saving saving = new Saving();
        saving.setNameSaving(request.getNameSaving());
        saving.setTargetAmount(request.getTargetAmount());
        saving.setDeadline(request.getDeadline());
        saving.setCreatedAt(LocalDateTime.now());
        saving.setUser(user);
        savingRepository.save(saving);

        return toSavingResponse(saving);
    }

    @Transactional(readOnly = true)
    public SavingResponse getSaving(String email,String savingId){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"saving not found"));

        return toSavingResponse(saving);
    }

    @Transactional
    public void createSavingTransaction(String email, String savingId,@Valid CreateSavingTransactionRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"saving not found"));

        SavingTransaction transaction = new SavingTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSaving(saving);

        savingTransactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public SavingProgressResponse getSavingProgress(String email,String savingId){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"saving not found"));

        return toSavingProgressResponse(saving);
    }

    @Transactional(readOnly = true)
    public Page<SavingResponse> listSaving(String email,@Valid ListSavingRequest request){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Specification<Saving> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            if (Objects.nonNull(request.getNameSaving())){
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("nameSaving"),"%" + request.getNameSaving() + "%")
                ));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Saving> savings = savingRepository.findAll(specification,pageable);
        List<SavingResponse> savingResponses = savings.getContent().stream()
                .map(this::toSavingResponse)
                .toList();

        return new PageImpl<>(savingResponses,pageable,savings.getTotalElements());
    }

    @Transactional
    public SavingResponse editSaving(String email,String savingId,@Valid CreateSavingRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"saving not found"));

        saving.setNameSaving(request.getNameSaving());
        saving.setTargetAmount(request.getTargetAmount());
        saving.setDeadline(request.getDeadline());
        savingRepository.save(saving);

        return toSavingResponse(saving);
    }

    @Transactional
    public void deleteSaving(String email,String savingId){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Saving saving = savingRepository.findByUserAndSavingId(user,savingId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"saving not found"));

        savingRepository.delete(saving);
    }

    // Method Helper
    private SavingResponse toSavingResponse(Saving saving){
        return SavingResponse.builder()
                .savingId(saving.getSavingId())
                .nameSaving(saving.getNameSaving())
                .targetAmount(saving.getTargetAmount())
                .deadline(saving.getDeadline())
                .CreatedAt(saving.getCreatedAt())
                .build();
    }

    private SavingProgressResponse toSavingProgressResponse(Saving saving){

        Long currentBalance = savingTransactionRepository.sumBySavingId(saving.getSavingId());

        if (currentBalance == null){
            currentBalance = 0L;
        }

        Long target = saving.getTargetAmount();

        Long remainingAmount = Math.max(target - currentBalance, 0);

        double progressPercentage = 0;

        if (target != null && target > 0) {
            progressPercentage = Math.min(
                    (double) currentBalance / target * 100,
                    100
            );
        }

        return SavingProgressResponse.builder()
                .savingId(saving.getSavingId())
                .nameSaving(saving.getNameSaving())
                .targetAmount(saving.getTargetAmount())
                .currentBalance(currentBalance)
                .progressPercentage(progressPercentage)
                .remainingAmount(remainingAmount)
                .build();
    }
}
