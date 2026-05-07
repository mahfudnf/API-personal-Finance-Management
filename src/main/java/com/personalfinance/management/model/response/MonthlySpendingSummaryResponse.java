package com.personalfinance.management.model.response;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.constant.StatusSpent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlySpendingSummaryResponse {
    private String summaryId;
    private String userId;
    private String limitId;
    private Long totalSpent;
    private StatusSpent statusSpent;
    private MonthValue monthValue;
    private Integer yearValue;
}
