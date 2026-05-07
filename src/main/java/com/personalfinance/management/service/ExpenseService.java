package com.personalfinance.management.service;

import com.personalfinance.management.model.request.CreateExpenseRequest;
import com.personalfinance.management.model.response.ExpenseResponse;
import com.personalfinance.management.model.request.ListExpenseRequest;
import com.personalfinance.management.model.request.UpdateExpenseRequest;
import org.springframework.data.domain.Page;

public interface ExpenseService {

    ExpenseResponse createExpense(CreateExpenseRequest request);
    ExpenseResponse getExpense(String expenseId);
    Page<ExpenseResponse> listExpense(ListExpenseRequest request);
    ExpenseResponse editExpense(String expenseId,UpdateExpenseRequest request);
    void deleteExpense(String expenseId);
}
