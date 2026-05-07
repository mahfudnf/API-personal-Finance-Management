package com.personalfinance.management.service;

import com.personalfinance.management.model.request.CreateIncomeRequest;
import com.personalfinance.management.model.response.IncomeResponse;
import com.personalfinance.management.model.request.ListIncomeRequest;
import com.personalfinance.management.model.request.UpdateIncomeRequest;
import org.springframework.data.domain.Page;

public interface IncomeService {

    IncomeResponse createIncome(CreateIncomeRequest request);
    IncomeResponse getIncome(String incomeId);
    Page<IncomeResponse> listIncome(ListIncomeRequest request);
    IncomeResponse editIncome(String incomeId,UpdateIncomeRequest request);
    void deleteIncome(String incomeId);
}
