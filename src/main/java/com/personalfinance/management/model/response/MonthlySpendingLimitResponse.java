package com.personalfinance.management.model.response;

import com.personalfinance.management.constant.MonthValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlySpendingLimitResponse {
    private String limitId;
    private String userId;
    private Long limitValue;
    private MonthValue monthValue;
    private Integer yearValue;
    private boolean isActive;
}
