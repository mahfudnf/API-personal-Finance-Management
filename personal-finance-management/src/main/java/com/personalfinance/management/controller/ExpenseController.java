package com.personalfinance.management.controller;

import com.personalfinance.management.model.PagingResponse;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.expense.CreateExpenseRequest;
import com.personalfinance.management.model.expense.ExpenseResponse;
import com.personalfinance.management.model.expense.ListExpenseRequest;
import com.personalfinance.management.model.expense.UpdateExpenseRequest;
import com.personalfinance.management.service.ExpenseService;
import com.personalfinance.management.service.impl.ExpenseServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(
            path = "/api/expenses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ExpenseResponse> createExpense(@AuthenticationPrincipal User user, @RequestBody @Valid CreateExpenseRequest request){
        ExpenseResponse response = expenseService.createExpense(user.getUsername(), request);
        return WebResponse.<ExpenseResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/expenses/{expenseId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ExpenseResponse> getExpense(@AuthenticationPrincipal User user,@PathVariable("expenseId") String expenseId){
        ExpenseResponse response = expenseService.getExpense(user.getUsername(), expenseId);
        return WebResponse.<ExpenseResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/expenses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ExpenseResponse>> listExpense(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "category",required = false) String category,
            @RequestParam(value = "page", required = false,defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue = "10") Integer size
    ){
        ListExpenseRequest request = ListExpenseRequest.builder()
                .category(category)
                .page(page)
                .size(size)
                .build();

        Page<ExpenseResponse> expenseResponses = expenseService.listExpense(user.getUsername(), request);
        return WebResponse.<List<ExpenseResponse>>builder()
                .data(expenseResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(expenseResponses.getNumber())
                        .totalPage(expenseResponses.getTotalPages())
                        .size(expenseResponses.getSize())
                        .build())
                .build();

    }

    @PatchMapping(
            path = "/api/expenses/{expenseId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ExpenseResponse> editExpense(
            @AuthenticationPrincipal User user,
            @PathVariable("expenseId") String expenseId,
            @RequestBody @Valid UpdateExpenseRequest request){

        ExpenseResponse response = expenseService.editExpense(user.getUsername(), expenseId,request);
        return WebResponse.<ExpenseResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/api/expenses/{expenseId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteExpense(@AuthenticationPrincipal User user,@PathVariable("expenseId") String expenseId){
        expenseService.deleteExpense(user.getUsername(), expenseId);
        return WebResponse.<String>builder().data("OK").build();
    }
}
