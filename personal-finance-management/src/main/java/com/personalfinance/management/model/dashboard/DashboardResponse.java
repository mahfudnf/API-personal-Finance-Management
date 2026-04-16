package com.personalfinance.management.model.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {

    private Long totalIncome;

    private Long totalExpense;

    private Long currentBalance;

}
