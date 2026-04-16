package com.personalfinance.management.controller;

import com.personalfinance.management.model.PagingResponse;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.income.CreateIncomeRequest;
import com.personalfinance.management.model.income.IncomeResponse;
import com.personalfinance.management.model.income.ListIncomeRequest;
import com.personalfinance.management.model.income.UpdateIncomeRequest;
import com.personalfinance.management.service.IncomeService;
import com.personalfinance.management.service.impl.IncomeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @PostMapping(
            path = "/api/incomes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<IncomeResponse> createIncome(@AuthenticationPrincipal User user, @RequestBody @Valid CreateIncomeRequest request){
        IncomeResponse response = incomeService.createIncome(user.getUsername(), request);
        return WebResponse.<IncomeResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/incomes/{incomeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<IncomeResponse> getIncome(@AuthenticationPrincipal User user,@PathVariable("incomeId") String incomeId){
        IncomeResponse response = incomeService.getIncome(user.getUsername(),incomeId);
        return WebResponse.<IncomeResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/incomes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<IncomeResponse>> listIncome(
            @AuthenticationPrincipal User user,
            @RequestParam (value = "category",required = false) String category,
            @RequestParam(value = "page", required = false,defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue = "10") Integer size){

        ListIncomeRequest request = ListIncomeRequest.builder()
                .category(category)
                .page(page)
                .size(size)
                .build();

        Page<IncomeResponse> incomeResponses = incomeService.listIncome(user.getUsername(), request);
        return WebResponse.<List<IncomeResponse>>builder()
                .data(incomeResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(incomeResponses.getNumber())
                        .totalPage(incomeResponses.getTotalPages())
                        .size(incomeResponses.getSize())
                        .build())
                .build();
    }

    @PatchMapping(
            path = "/api/incomes/{incomeId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<IncomeResponse> editIncome(
            @AuthenticationPrincipal User user,@PathVariable("incomeId") String incomeId,@RequestBody @Valid UpdateIncomeRequest request){

        IncomeResponse response = incomeService.editIncome(user.getUsername(), incomeId,request);
        return WebResponse.<IncomeResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/api/incomes/{incomeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteIncome(@AuthenticationPrincipal User user,@PathVariable("incomeId") String incomeId){

        incomeService.deleteIncome(user.getUsername(), incomeId);
        return WebResponse.<String>builder().data("OK").build();
    }

}
