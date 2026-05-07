package com.personalfinance.management.service.impl;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.constant.StatusSpent;
import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.MonthlySpendingLimit;
import com.personalfinance.management.entity.MonthlySpendingSummary;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.exception.custom.ResourceNotFoundException;
import com.personalfinance.management.model.request.ListSpendingSummaryRequest;
import com.personalfinance.management.model.response.MonthlySpendingSummaryResponse;
import com.personalfinance.management.repository.ExpenseRepository;
import com.personalfinance.management.repository.MonthlySpendingLimitRepository;
import com.personalfinance.management.repository.MonthlySpendingSummaryRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.MonthlySpendingSummaryService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class MonthlySpendingSummaryServiceImpl implements MonthlySpendingSummaryService {
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final MonthlySpendingLimitRepository monthlySpendingLimitRepository;
    private final MonthlySpendingSummaryRepository monthlySpendingSummaryRepository;
    private final ResponseUtils responseUtils;

    public MonthlySpendingSummaryServiceImpl(UserRepository userRepository, ExpenseRepository expenseRepository, MonthlySpendingLimitRepository monthlySpendingLimitRepository, MonthlySpendingSummaryRepository monthlySpendingSummaryRepository, ResponseUtils responseUtils) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.monthlySpendingLimitRepository = monthlySpendingLimitRepository;
        this.monthlySpendingSummaryRepository = monthlySpendingSummaryRepository;
        this.responseUtils = responseUtils;
    }

    @Override
    public MonthlySpendingSummaryResponse generateSpendingSummary(String limitId, Integer monthValue, Integer yearValue) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        MonthlySpendingLimit limit = monthlySpendingLimitRepository
                .findByUserAndLimitId(user,limitId)
                .orElseThrow(() -> new ResourceNotFoundException("limit dengan id " + limitId + " tidak ditemukan"));

        Long total = expenseRepository.sumByUserIdAndMonthValueAndYearValue(
                user.getUserId(),
                monthValue,
                yearValue
        );

        if (total == null) total = 0L;
        StatusSpent statusSpent = responseUtils.calculateStatus(total,limit.getLimitValue());

        MonthlySpendingSummary summary = monthlySpendingSummaryRepository
                .findByUserAndMonthlySpendingLimit(user, limit)
                .orElse(new MonthlySpendingSummary());

        summary.setTotalSpent(total);
        summary.setStatusSpent(statusSpent);
        summary.setMonthValue(MonthValue.fromInt(monthValue));
        summary.setYearValue(yearValue);
        summary.setUser(user);
        summary.setMonthlySpendingLimit(limit);

        monthlySpendingSummaryRepository.save(summary);

        return responseUtils.toMonthlySpendingSummaryResponse(summary);
    }

    @Override
    public MonthlySpendingSummaryResponse getCurrentMonthSummaryByUser() {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {});

        LocalDate now = LocalDate.now();
        MonthValue monthEnum = MonthValue.fromInt(now.getMonthValue());
        int currentYear = now.getYear();

        MonthlySpendingSummary summary = monthlySpendingSummaryRepository.findByUserAndMonthValueAndYearValue(user,monthEnum,currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("summary di bulan ini tidak ditemukan"));

        return responseUtils.toMonthlySpendingSummaryResponse(summary);
    }

    @Override
    public MonthlySpendingSummaryResponse getSpendingSummaryByUserByMonthAndYear(Integer monthValue,Integer yearValue) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {});

        MonthValue monthEnum = MonthValue.fromInt(monthValue);
        int currentYear = yearValue;

        MonthlySpendingSummary summary = monthlySpendingSummaryRepository.findByUserAndMonthValueAndYearValue(
                user,monthEnum,currentYear
        ) .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Limit bulan %s tahun %d tidak ditemukan", monthEnum, currentYear)));

        return responseUtils.toMonthlySpendingSummaryResponse(summary);
    }

    @Override
    public MonthlySpendingSummaryResponse recalculateSpendingSummary(String limitId, Integer monthValue, Integer yearValue) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {});

        MonthlySpendingLimit limit = monthlySpendingLimitRepository
                .findByUserAndLimitId(user,limitId)
                .orElseThrow(() -> new ResourceNotFoundException("limit dengan id " + limitId + " tidak ditemukan"));

        MonthlySpendingSummary summary = monthlySpendingSummaryRepository
                .findByUserAndMonthlySpendingLimitAndMonthValueAndYearValue(
                        user, limit, MonthValue.fromInt(monthValue), yearValue)
                .orElse(new MonthlySpendingSummary());

        Long total = expenseRepository.sumByUserIdAndMonthValueAndYearValue(
                user.getUserId(),
                monthValue,
                yearValue
        );

        if (total == null) total = 0L;
        StatusSpent status = responseUtils.calculateStatus(total, limit.getLimitValue());

        summary.setTotalSpent(total);
        summary.setStatusSpent(status);
        summary.setMonthValue(MonthValue.fromInt(monthValue));
        summary.setYearValue(yearValue);
        summary.setUser(user);
        summary.setMonthlySpendingLimit(limit);

        monthlySpendingSummaryRepository.save(summary);

        return responseUtils.toMonthlySpendingSummaryResponse(summary);
    }
}
