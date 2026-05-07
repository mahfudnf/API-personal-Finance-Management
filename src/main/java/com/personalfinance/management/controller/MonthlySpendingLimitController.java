package com.personalfinance.management.controller;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.model.request.*;
import com.personalfinance.management.model.response.*;
import com.personalfinance.management.service.MonthlySpendingLimitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MonthlySpendingLimitController {
    private final MonthlySpendingLimitService monthlySpendingLimitService;

    public MonthlySpendingLimitController(MonthlySpendingLimitService monthlySpendingLimitService) {
        this.monthlySpendingLimitService = monthlySpendingLimitService;
    }

    @PostMapping(
            path = "/api/limits",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingLimitResponse>> createSpendingLimit(
            @RequestBody @Valid CreateSpendingLimitRequest request){

        MonthlySpendingLimitResponse response = monthlySpendingLimitService.createSpendingLimit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<MonthlySpendingLimitResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/limits/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingLimitResponse>> getCurrentLimit(){

        MonthlySpendingLimitResponse response = monthlySpendingLimitService.getCurrentLimit();
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<MonthlySpendingLimitResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/limits/{monthValue}/{yearValue}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingLimitResponse>> getLimitByMonthAndYear(
            @PathVariable("monthValue") MonthValue month,
            @PathVariable("yearValue") Integer year)
    {

        MonthlySpendingLimitResponse response = monthlySpendingLimitService.getLimitByMonthAndYear(month,year);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<MonthlySpendingLimitResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/limits",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<MonthlySpendingLimitResponse>>> listLimits(
            @RequestParam (value = "limitValue",required = false) Long limitValue,
            @RequestParam(value = "page", required = false,defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue = "10") Integer size){

        ListSpendingLimitRequest request = ListSpendingLimitRequest.builder()
                .limitValue(limitValue)
                .page(page)
                .size(size)
                .build();

        Page<MonthlySpendingLimitResponse> responses = monthlySpendingLimitService.listLimits(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        WebResponse.<List<MonthlySpendingLimitResponse>>builder()
                                .data(responses.getContent())
                                .paging(PagingResponse.builder()
                                        .currentPage(responses.getNumber())
                                        .totalPage(responses.getTotalPages())
                                        .size(responses.getSize())
                                        .build())
                                .build()
                );
    }

    @PutMapping(
            path = "/api/limits/{limitId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingLimitResponse>> updateLimit(
            @PathVariable("limitId") String limitId,
            @RequestBody @Valid UpdateSpendingLimitRequest request){

        MonthlySpendingLimitResponse response = monthlySpendingLimitService.updateLimit(limitId,request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<MonthlySpendingLimitResponse>builder().data(response).build());
    }
}
