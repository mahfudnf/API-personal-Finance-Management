package com.personalfinance.management.model.expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseResponse {

    private String expenseId;

    private Long amount;

    private String category;

    private LocalDateTime createdAt;

    private String description;
}
