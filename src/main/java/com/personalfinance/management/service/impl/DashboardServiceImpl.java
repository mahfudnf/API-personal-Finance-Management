package com.personalfinance.management.service.impl;

import com.personalfinance.management.constant.Status;
import com.personalfinance.management.entity.Saving;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.response.DashboardResponse;
import com.personalfinance.management.model.response.SavingProgressResponse;
import com.personalfinance.management.repository.*;
import com.personalfinance.management.service.DashboardService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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

    @Autowired
    private MonthlySpendingLimitRepository limitRepository;

    @Autowired
    private ResponseUtils responseUtils;


    @Override
    public DashboardResponse getDashboard() {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {});

        // 1. Income & Expense
        Long totalIncome = incomeRepository.sumByUserId(user.getUserId());
        Long totalExpense = expenseRepository.sumByUserId(user.getUserId());

        if (totalIncome == null) totalIncome = 0L;
        if (totalExpense == null) totalExpense = 0L;

        Long currentBalance = totalIncome - totalExpense;

        // 2. Spending Limit
        Long spendingLimit = limitRepository.findLimitValueByUserId(user.getUserId());
        if (spendingLimit == null) spendingLimit = 0L;

        // 3. Saving Progress
        List<Saving> savings = savingRepository.findByUser(user);

        List<SavingProgressResponse> savingProgressList = savings.stream().map(saving -> {
            Long savedAmount = savingTransactionRepository
                    .sumBySavingId(saving.getSavingId());

            if (savedAmount == null) savedAmount = 0L;

            Long target = saving.getTargetSaving();
            Long remaining = Math.max(target - savedAmount, 0);

            double progress = 0;
            if (target != null && target > 0) {
                progress = Math.min((double) savedAmount / target * 100, 100);
            }

            Status status = responseUtils.statusActual(savedAmount,target);

            return SavingProgressResponse.builder()
                    .savingId(saving.getSavingId())
                    .userId(saving.getUser().getUserId())
                    .nameSaving(saving.getNameSaving())
                    .targetSaving(target)
                    .currentAmount(savedAmount)
                    .status(status)
                    .remainingAmount(remaining)
                    .progressPercentage(progress)
                    .build();

        }).toList();

        // 4. Build Response
        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentBalance(currentBalance)
                .spendingLimit(spendingLimit)
                .savingProgress(savingProgressList)
                .build();
    }
}
