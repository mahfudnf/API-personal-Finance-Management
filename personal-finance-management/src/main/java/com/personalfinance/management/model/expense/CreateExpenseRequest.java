package com.personalfinance.management.model.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateExpenseRequest {

    @NotNull
    private Long amount;

    @NotBlank
    @Size(max = 100)
    private String category;

    @Size(max = 500)
    private String description;
}
