package com.personalfinance.management.model.saving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingResponse {

    private String savingId;

    private String nameSaving;

    private Long targetAmount;

    private LocalDate deadline;

    private LocalDateTime CreatedAt;
}
