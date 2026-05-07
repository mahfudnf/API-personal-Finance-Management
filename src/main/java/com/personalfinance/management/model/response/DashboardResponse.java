package com.personalfinance.management.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    private Long totalIncome;
    private Long totalExpense;
    private Long currentBalance;

    private Long spendingLimit;

    private List<SavingProgressResponse> savingProgress;

}
