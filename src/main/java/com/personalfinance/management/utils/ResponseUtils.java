package com.personalfinance.management.utils;

import com.personalfinance.management.constant.Status;
import com.personalfinance.management.constant.StatusSpent;
import com.personalfinance.management.entity.*;
import com.personalfinance.management.model.response.*;
import com.personalfinance.management.repository.SavingTransactionRepository;
import com.personalfinance.management.security.service.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ResponseUtils {
    private final JwtService jwtService;
    private final SavingTransactionRepository savingTransactionRepository;

    public ResponseUtils(JwtService jwtService, SavingTransactionRepository savingTransactionRepository) {
        this.jwtService = jwtService;
        this.savingTransactionRepository = savingTransactionRepository;
    }

    // Layer Service
    // Helper current user
    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new BadCredentialsException("Unauthorized");
        }

        return auth.getName();
    }

    // Helper user response
    public UserResponse toUserResponse(UserEntity user){
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    // Helper token response
    public TokenResponse toTokenResponse(UserDetails userDetails){
        String token = jwtService.generateToken(userDetails);
        long expiredAt = System.currentTimeMillis() + jwtService.getExpirationTime();

        return TokenResponse.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();
    }

    // Helper to income response
    public IncomeResponse toIncomeResponse(Income income){
        return IncomeResponse.builder()
                .incomeId(income.getIncomeId())
                .userId(income.getUser().getUserId())
                .amount(income.getAmount())
                .category(income.getCategory())
                .description(income.getDescription())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }

    // Helper to expense response
    public ExpenseResponse toExpenseResponse(Expense expense){
        return ExpenseResponse.builder()
                .expenseId(expense.getExpenseId())
                .userId(expense.getUser().getUserId())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

    // Helper to saving response
    public SavingResponse toSavingResponse(Saving saving){
        Long currentSavingAmount =
                Optional.ofNullable(
                        savingTransactionRepository.sumBySavingId(saving.getSavingId())
                ).orElse(0L);
        Long target =
                Optional.ofNullable(saving.getTargetSaving())
                        .orElse(0L);
        Status status = statusActual(currentSavingAmount,target);

        return SavingResponse.builder()
                .savingId(saving.getSavingId())
                .userId(saving.getUser().getUserId())
                .nameSaving(saving.getNameSaving())
                .targetSaving(saving.getTargetSaving())
                .deadlineSaving(saving.getDeadlineSaving())
                .currentAmount(currentSavingAmount)
                .status(status)
                .createdAt(saving.getCreatedAt())
                .updatedAt(saving.getUpdatedAt())
                .build();
    }

    // Private Helper set actual status saving
    public Status statusActual(Long currentAmount, Long targetAmount){
        if (currentAmount >= targetAmount){
            return Status.SUCCESS;
        }
        return Status.PROGRESS;
    }

    // Helper to saving progress response
    public SavingProgressResponse toSavingProgressResponse(Saving saving){
        Long currentBalance = savingTransactionRepository.sumBySavingId(saving.getSavingId());
        Long target = Optional.ofNullable(saving.getTargetSaving()).orElse(0L);

        Long remainingAmount = Math.max(target - currentBalance, 0);
        double progressPercentage = 0;

        if (target != null && target > 0) {
            progressPercentage = Math.min(
                    (double) currentBalance / target * 100,
                    100
            );
        }

        Status status = statusActual(currentBalance,target);

        return SavingProgressResponse.builder()
                .savingId(saving.getSavingId())
                .userId(saving.getUser().getUserId())
                .nameSaving(saving.getNameSaving())
                .targetSaving(saving.getTargetSaving())
                .currentAmount(currentBalance)
                .status(status)
                .progressPercentage(progressPercentage)
                .remainingAmount(remainingAmount)
                .build();
    }

    // Helper to monthly spending limit
    public MonthlySpendingLimitResponse toMonthlySpendingLimit(MonthlySpendingLimit limit){
        boolean isActive = isCurrentMonth(limit);

        return MonthlySpendingLimitResponse.builder()
                .limitId(limit.getLimitId())
                .userId(limit.getUser().getUserId())
                .limitValue(limit.getLimitValue())
                .monthValue(limit.getMonthValue())
                .yearValue(limit.getYearValue())
                .isActive(isActive)
                .build();
    }

    // Private Helper set is active monthly spending limit
    private boolean isCurrentMonth(MonthlySpendingLimit limit) {
        LocalDate now = LocalDate.now();

        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue(); // 1-12

        return limit.getYearValue() == currentYear
                && limit.getMonthValue().getValue() == currentMonth;
    }

    // Helper calculation status spent summary
    public StatusSpent calculateStatus(Long total, Long limitAmount) {
        if (total == null) total = 0L;

        double percentage = (double) total / limitAmount;

        if (percentage <= 0.8) {
            return StatusSpent.SAFE;
        } else if (percentage <= 1.0) {
            return StatusSpent.WARNING;
        } else {
            return StatusSpent.EXCEEDED;
        }
    }

    // Helper to monthly spending summary response
    public MonthlySpendingSummaryResponse toMonthlySpendingSummaryResponse(MonthlySpendingSummary summary){
        return MonthlySpendingSummaryResponse.builder()
                .summaryId(summary.getSummaryId())
                .userId(summary.getUser().getUserId())
                .limitId(summary.getMonthlySpendingLimit().getLimitId())
                .totalSpent(summary.getTotalSpent())
                .statusSpent(summary.getStatusSpent())
                .monthValue(summary.getMonthValue())
                .yearValue(summary.getYearValue())
                .build();
    }

}
