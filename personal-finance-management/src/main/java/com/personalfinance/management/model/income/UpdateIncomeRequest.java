package com.personalfinance.management.model.income;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateIncomeRequest {

    @Positive(message = "Amount must be greater than 0")
    private Long amount;

    @Size(max = 100)
    private String category;

    @Size(max = 500)
    private String description;
}
