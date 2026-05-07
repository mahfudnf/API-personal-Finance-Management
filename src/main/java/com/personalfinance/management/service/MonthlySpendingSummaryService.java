package com.personalfinance.management.service;

import com.personalfinance.management.model.response.MonthlySpendingSummaryResponse;

public interface MonthlySpendingSummaryService {
    MonthlySpendingSummaryResponse generateSpendingSummary(
            String limitId, Integer monthValue, Integer yearValue);

    MonthlySpendingSummaryResponse getCurrentMonthSummaryByUser();

    MonthlySpendingSummaryResponse getSpendingSummaryByUserByMonthAndYear(
            Integer monthValue,Integer yearValue);

    MonthlySpendingSummaryResponse recalculateSpendingSummary(
            String limitId,Integer monthValue, Integer yearValue);
}
