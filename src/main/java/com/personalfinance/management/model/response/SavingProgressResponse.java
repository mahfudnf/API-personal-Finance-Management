package com.personalfinance.management.model.response;

import com.personalfinance.management.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingProgressResponse {
    private String savingId;
    private String userId;
    private String nameSaving;
    private Long targetSaving;
    private Long currentAmount;
    private Status status;
    private double progressPercentage;
    private Long remainingAmount;
}
