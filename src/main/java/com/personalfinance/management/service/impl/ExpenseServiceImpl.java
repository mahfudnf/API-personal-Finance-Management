package com.personalfinance.management.service.impl;

import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.exception.custom.ResourceNotFoundException;
import com.personalfinance.management.model.request.CreateExpenseRequest;
import com.personalfinance.management.model.response.ExpenseResponse;
import com.personalfinance.management.model.request.ListExpenseRequest;
import com.personalfinance.management.model.request.UpdateExpenseRequest;
import com.personalfinance.management.repository.ExpenseRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.ExpenseService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ResponseUtils responseUtils;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository, ResponseUtils responseUtils) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.responseUtils = responseUtils;
    }

    public ExpenseResponse createExpense(CreateExpenseRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setUser(user);
        expenseRepository.save(expense);

        return responseUtils.toExpenseResponse(expense);
    }

    public ExpenseResponse getExpense(String expenseId){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Expense expense = expenseRepository.findByUserAndExpenseId(user,expenseId)
                .orElseThrow(()-> new ResourceNotFoundException("Income dengan id " + expenseId + " tidak ditemukan"));

        return responseUtils.toExpenseResponse(expense);
    }

    public Page<ExpenseResponse> listExpense(ListExpenseRequest request) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {});

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Expense> expenses;
        if (request.getCategory() != null) {
            expenses = expenseRepository.findByUserAndCategoryContaining(
                    user,
                    request.getCategory(),
                    pageable
            );
        } else {
            expenses = expenseRepository.findByUser(user, pageable);
        }

        return expenses.map(responseUtils::toExpenseResponse);
    }

    public ExpenseResponse editExpense(String expenseId,UpdateExpenseRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Expense expense = expenseRepository.findByUserAndExpenseId(user,expenseId)
                .orElseThrow(()-> new ResourceNotFoundException("Income dengan id " + expenseId + " tidak ditemukan"));

        if (Objects.nonNull(request.getAmount())){
            expense.setAmount(request.getAmount());
        }

        if (Objects.nonNull(request.getCategory())){
            expense.setCategory(request.getCategory());
        }

        if (Objects.nonNull(request.getDescription())){
            expense.setDescription(request.getDescription());
        }

        expenseRepository.save(expense);

        return responseUtils.toExpenseResponse(expense);
    }

    public void deleteExpense(String expenseId){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Expense expense = expenseRepository.findByUserAndExpenseId(user,expenseId)
                .orElseThrow(()-> new ResourceNotFoundException("Income dengan id " + expenseId + " tidak ditemukan"));

        expenseRepository.delete(expense);
    }

}
