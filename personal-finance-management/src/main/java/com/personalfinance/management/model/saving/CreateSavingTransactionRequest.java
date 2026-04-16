package com.personalfinance.management.model.saving;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSavingTransactionRequest {

    @NotNull
    @Positive(message = "Amount must be greater than 0")
    private Long amount;

}
