package com.personalfinance.management.model.request;

import com.personalfinance.management.constant.MonthValue;
import jakarta.validation.constraints.NotBlank;
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
public class CreateSpendingLimitRequest {
    @NotNull
    @Positive(message = "Jumlah harus lebih besar dari 0")
    private Long limitValue;

    @NotNull
    private MonthValue monthValue;

    @NotNull
    private Integer yearValue;
}
