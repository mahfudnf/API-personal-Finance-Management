package com.personalfinance.management.model.request;

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
    @Positive(message = "Jumlah harus lebih besar dari 0")
    private Long amount;

    @Size(max = 225)
    private String category;

    @Size(max = 500)
    private String description;
}
