package com.personalfinance.management.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListSpendingLimitRequest {
    private Long limitValue;
    private Integer page;
    private Integer size;
}
