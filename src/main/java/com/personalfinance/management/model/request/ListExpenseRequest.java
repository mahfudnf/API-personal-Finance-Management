package com.personalfinance.management.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListExpenseRequest {
    @Size(max = 255)
    private String category;
    private Integer page;
    private Integer size;
}
