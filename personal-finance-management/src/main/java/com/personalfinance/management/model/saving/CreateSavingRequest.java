package com.personalfinance.management.model.saving;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSavingRequest {

    @NotBlank
    @Size(max = 200)
    private String nameSaving;

    @NotNull
    @Positive(message = "Amount must be greater than 0")
    private Long targetAmount;

    private LocalDate deadline;
}
