package com.personalfinance.management.model.income;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeResponse {

    private String incomeId;

    private Long amount;

    private String category;

    private LocalDateTime createdAt;

    private String description;
}
