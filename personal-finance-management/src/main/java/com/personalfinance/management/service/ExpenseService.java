package com.personalfinance.management.service;

import com.personalfinance.management.model.expense.CreateExpenseRequest;
import com.personalfinance.management.model.expense.ExpenseResponse;
import com.personalfinance.management.model.expense.ListExpenseRequest;
import com.personalfinance.management.model.expense.UpdateExpenseRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ExpenseService {

    ExpenseResponse createExpense(String email, @Valid CreateExpenseRequest request);
    ExpenseResponse getExpense(String email,String expenseId);
    Page<ExpenseResponse> listExpense(String email, @Valid ListExpenseRequest request);
    ExpenseResponse editExpense(String email, String expenseId,@Valid UpdateExpenseRequest request);
    void deleteExpense(String email,String expenseId);
}
