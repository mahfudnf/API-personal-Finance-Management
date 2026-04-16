package com.personalfinance.management.service.impl;

import com.personalfinance.management.entity.Saving;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.dashboard.DashboardResponse;
import com.personalfinance.management.model.saving.SavingProgressResponse;
import com.personalfinance.management.repository.*;
import com.personalfinance.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private SavingTransactionRepository savingTransactionRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Long totalIncome = incomeRepository.sumByUserId(user.getUserId());
        Long totalExpense = expenseRepository.sumByUserId(user.getUserId());

        if (totalIncome == null) totalIncome = 0L;
        if (totalExpense == null) totalExpense = 0L;

        Long currentBalance = totalIncome - totalExpense;

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentBalance(currentBalance)
                .build();
    }

    @Transactional
    public List<SavingProgressResponse> getSavingProgress(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        List<Saving> savings = savingRepository.findByUser(user);

        return savings.stream().map(this::mapToProgress).toList();
    }

    // Method Helper
    private SavingProgressResponse mapToProgress(Saving saving) {

        Long currentBalance =
                savingTransactionRepository.sumBySavingId(saving.getSavingId());

        if (currentBalance == null) currentBalance = 0L;

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
                .targetAmount(target)
                .currentBalance(currentBalance)
                .progressPercentage(progressPercentage)
                .remainingAmount(remainingAmount)
                .build();
    }

}
