package com.personalfinance.management.service;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.model.request.CreateSpendingLimitRequest;
import com.personalfinance.management.model.request.ListSpendingLimitRequest;
import com.personalfinance.management.model.request.UpdateSpendingLimitRequest;
import com.personalfinance.management.model.response.MonthlySpendingLimitResponse;
import org.springframework.data.domain.Page;

public interface MonthlySpendingLimitService {
    MonthlySpendingLimitResponse createSpendingLimit(CreateSpendingLimitRequest request);
    MonthlySpendingLimitResponse getCurrentLimit();
    MonthlySpendingLimitResponse getLimitByMonthAndYear(MonthValue month, Integer year);
    Page<MonthlySpendingLimitResponse> listLimits(ListSpendingLimitRequest request);
    MonthlySpendingLimitResponse updateLimit(String limitId,UpdateSpendingLimitRequest request);

}