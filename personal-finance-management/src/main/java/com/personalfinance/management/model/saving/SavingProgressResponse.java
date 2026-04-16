package com.personalfinance.management.model.saving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingProgressResponse {

    private String savingId;

    private String nameSaving;

    private Long targetAmount;

    private Long currentBalance;

    private double progressPercentage;

    private Long remainingAmount;
}
