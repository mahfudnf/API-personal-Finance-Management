package com.personalfinance.management.model.income;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateIncomeRequest {

    @NotNull
    @Positive(message = "Amount must be greater than 0")
    private Long amount;

    @NotBlank
    @Size(max = 100)
    private String category;

    @Size(max = 500)
    private String description;
}
