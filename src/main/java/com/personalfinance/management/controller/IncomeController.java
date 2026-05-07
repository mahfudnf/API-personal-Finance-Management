package com.personalfinance.management.controller;

import com.personalfinance.management.model.response.PagingResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.model.request.CreateIncomeRequest;
import com.personalfinance.management.model.response.IncomeResponse;
import com.personalfinance.management.model.request.ListIncomeRequest;
import com.personalfinance.management.model.request.UpdateIncomeRequest;
import com.personalfinance.management.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IncomeController {
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping(
            path = "/api/incomes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<IncomeResponse>> createIncome(
            @RequestBody @Valid CreateIncomeRequest request){

        IncomeResponse response = incomeService.createIncome(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<IncomeResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/incomes/{incomeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<IncomeResponse>> getIncome(@PathVariable("incomeId") String incomeId){

        IncomeResponse response = incomeService.getIncome(incomeId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<IncomeResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/incomes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<IncomeResponse>>> listIncome(
            @RequestParam (value = "category",required = false) String category,
            @RequestParam(value = "page", required = false,defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue = "10") Integer size){

        ListIncomeRequest request = ListIncomeRequest.builder()
                .category(category)
                .page(page)
                .size(size)
                .build();

        Page<IncomeResponse> incomeResponses = incomeService.listIncome(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        WebResponse.<List<IncomeResponse>>builder()
                                .data(incomeResponses.getContent())
                                .paging(PagingResponse.builder()
                                        .currentPage(incomeResponses.getNumber())
                                        .totalPage(incomeResponses.getTotalPages())
                                        .size(incomeResponses.getSize())
                                        .build())
                                .build()
                );
    }

    @PatchMapping(
            path = "/api/incomes/{incomeId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<IncomeResponse>> editIncome(
            @PathVariable("incomeId") String incomeId,@RequestBody @Valid UpdateIncomeRequest request){

        IncomeResponse response = incomeService.editIncome(incomeId,request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<IncomeResponse>builder().data(response).build());
    }

    @DeleteMapping(
            path = "/api/incomes/{incomeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteIncome(@PathVariable("incomeId") String incomeId){

        incomeService.deleteIncome(incomeId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<String>builder().data("OK").build());
    }

}
