package com.personalfinance.management.service.impl;

import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.expense.CreateExpenseRequest;
import com.personalfinance.management.model.expense.ExpenseResponse;
import com.personalfinance.management.model.expense.ListExpenseRequest;
import com.personalfinance.management.model.expense.UpdateExpenseRequest;
import com.personalfinance.management.repository.ExpenseRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.ExpenseService;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ExpenseResponse createExpense(String email,@Valid CreateExpenseRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setCreatedAt(LocalDateTime.now());
        expense.setDescription(request.getDescription());
        expense.setUser(user);
        expenseRepository.save(expense);

        return toExpenseResponse(expense);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpense(String email,String expenseId){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Expense expense = expenseRepository.findByUserAndExpenseId(user,expenseId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"expense not found"));

        return toExpenseResponse(expense);
    }

    @Transactional
    public Page<ExpenseResponse> listExpense(String email,@Valid ListExpenseRequest request){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Specification<Expense> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"),user));

            if (Objects.nonNull(request.getCategory())){
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("category"),"%" + request.getCategory() + "%")
                ));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Expense> expenses = expenseRepository.findAll(specification,pageable);
        List<ExpenseResponse> expenseResponses = expenses.getContent().stream()
                .map(this::toExpenseResponse)
                .toList();

        return new PageImpl<>(expenseResponses,pageable,expenses.getTotalElements());
    }

    @Transactional
    public ExpenseResponse editExpense(String email, String expenseId,@Valid UpdateExpenseRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));
        Expense expense = expenseRepository.findByUserAndExpenseId(user,expenseId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"expense not found"));

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

        return toExpenseResponse(expense);

    }

    @Transactional
    public void deleteExpense(String email,String expenseId){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));
        Expense expense = expenseRepository.findByUserAndExpenseId(user,expenseId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"expense not found"));

        expenseRepository.delete(expense);

    }

    // Method Helper
    private ExpenseResponse toExpenseResponse(Expense expense){
        return ExpenseResponse.builder()
                .expenseId(expense.getExpenseId())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .createdAt(expense.getCreatedAt())
                .description(expense.getDescription())
                .build();
    }

}
