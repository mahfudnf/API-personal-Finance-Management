package com.personalfinance.management.model.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateExpenseRequest {
    @NotNull
    @Positive(message = "Amount must be greater than 0")
    private Long amount;

    @NotBlank
    @Size(max = 255)
    private String category;

    @Size(max = 500)
    private String description;
}
