package com.personalfinance.management.model.request;

import com.personalfinance.management.constant.MonthValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListSpendingSummaryRequest {
    private MonthValue monthValue;
    private Integer yearValue;
    private Integer page;
    private Integer size;
}
