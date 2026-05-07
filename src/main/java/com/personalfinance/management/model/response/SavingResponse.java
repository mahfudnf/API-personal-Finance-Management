package com.personalfinance.management.model.response;

import com.personalfinance.management.constant.Status;
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
public class SavingResponse {
    private String savingId;
    private String userId;
    private String nameSaving;
    private Long targetSaving;
    private LocalDate deadlineSaving;
    private Long currentAmount;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
