package com.personalfinance.management.model.response;

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
    private String userId;
    private Long amount;
    private String category;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
