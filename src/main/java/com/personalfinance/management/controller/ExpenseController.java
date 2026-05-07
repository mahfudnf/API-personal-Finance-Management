package com.personalfinance.management.controller;

import com.personalfinance.management.model.response.PagingResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.model.request.CreateExpenseRequest;
import com.personalfinance.management.model.response.ExpenseResponse;
import com.personalfinance.management.model.request.ListExpenseRequest;
import com.personalfinance.management.model.request.UpdateExpenseRequest;
import com.personalfinance.management.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping(
            path = "/api/expenses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<ExpenseResponse>> createExpense(@RequestBody @Valid CreateExpenseRequest request){
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<ExpenseResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/expenses/{expenseId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<ExpenseResponse>> getExpense(@PathVariable("expenseId") String expenseId){
        ExpenseResponse response = expenseService.getExpense(expenseId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<ExpenseResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/expenses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<ExpenseResponse>>> listExpense(
            @RequestParam(value = "category",required = false) String category,
            @RequestParam(value = "page", required = false,defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue = "10") Integer size
    ){
        ListExpenseRequest request = ListExpenseRequest.builder()
                .category(category)
                .page(page)
                .size(size)
                .build();

        Page<ExpenseResponse> expenseResponses = expenseService.listExpense(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        WebResponse.<List<ExpenseResponse>>builder()
                                .data(expenseResponses.getContent())
                                .paging(PagingResponse.builder()
                                        .currentPage(expenseResponses.getNumber())
                                        .totalPage(expenseResponses.getTotalPages())
                                        .size(expenseResponses.getSize())
                                        .build())
                                .build()
                );
    }

    @PatchMapping(
            path = "/api/expenses/{expenseId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<ExpenseResponse>> editExpense(
            @PathVariable("expenseId") String expenseId,
            @RequestBody @Valid UpdateExpenseRequest request){

        ExpenseResponse response = expenseService.editExpense(expenseId,request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<ExpenseResponse>builder().data(response).build());
    }

    @DeleteMapping(
            path = "/api/expenses/{expenseId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteExpense(@PathVariable("expenseId") String expenseId){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<String>builder().data("OK").build());
    }
}
