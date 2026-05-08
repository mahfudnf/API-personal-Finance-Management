package com.personalfinance.management.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSavingRequest {
    @NotBlank
    @Size(max = 255)
    private String nameSaving;

    @NotNull
    @Positive(message = "Jumlah harus lebih besar dari 0")
    private Long targetSaving;

    @NotNull
    private LocalDate deadlineSaving;
}
