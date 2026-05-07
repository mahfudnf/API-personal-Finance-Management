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
public class ListSavingRequest {
    @Size(max = 255)
    private String nameSaving;
    private Integer page;
    private Integer size;
}
