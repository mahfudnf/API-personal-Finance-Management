package com.personalfinance.management.service;

import com.personalfinance.management.model.income.CreateIncomeRequest;
import com.personalfinance.management.model.income.IncomeResponse;
import com.personalfinance.management.model.income.ListIncomeRequest;
import com.personalfinance.management.model.income.UpdateIncomeRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface IncomeService {

    IncomeResponse createIncome(String email, @Valid CreateIncomeRequest request);
    IncomeResponse getIncome(String email,String incomeId);
    Page<IncomeResponse> listIncome(String email, @Valid ListIncomeRequest request);
    public IncomeResponse editIncome(String email, String incomeId,@Valid UpdateIncomeRequest request);
    void deleteIncome(String email,String incomeId);

}
