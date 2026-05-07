package com.personalfinance.management.controller;

import com.personalfinance.management.model.response.MonthlySpendingSummaryResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.service.MonthlySpendingSummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class MonthlySpendingSummaryController {
    private final MonthlySpendingSummaryService monthlySpendingSummaryService;

    public MonthlySpendingSummaryController(MonthlySpendingSummaryService monthlySpendingSummaryService) {
        this.monthlySpendingSummaryService = monthlySpendingSummaryService;
    }

    @PostMapping(
            path = "/api/limits/{limitId}/summaries",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingSummaryResponse>> generateSpendingSummary(
            @PathVariable("limitId") String limitId,
            @RequestParam Integer monthValue,
            @RequestParam Integer yearValue
    ){

        MonthlySpendingSummaryResponse response = monthlySpendingSummaryService.generateSpendingSummary(
                limitId,monthValue,yearValue
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<MonthlySpendingSummaryResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/summaries/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingSummaryResponse>> getCurrentMonthSummaryByUser(){

        MonthlySpendingSummaryResponse response = monthlySpendingSummaryService.getCurrentMonthSummaryByUser();
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<MonthlySpendingSummaryResponse>builder().data(response).build());
    }


    @GetMapping(
            path = "/api/summaries/{monthValue}/{yearValue}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingSummaryResponse>> getSpendingSummaryByUserByMonthAndYear(
            @PathVariable ("monthValue") Integer monthValue,
            @PathVariable ("yearValue") Integer yearValue
        ){

        MonthlySpendingSummaryResponse response = monthlySpendingSummaryService.getSpendingSummaryByUserByMonthAndYear(
                monthValue,yearValue
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<MonthlySpendingSummaryResponse>builder().data(response).build());
    }

    @PostMapping(
            path = "/api/limits/{limitId}/summaries/recalculate",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MonthlySpendingSummaryResponse>> recalculateSpendingSummary(
            @PathVariable("limitId") String limitId,
            @RequestParam Integer monthValue,
            @RequestParam Integer yearValue
        ){

        MonthlySpendingSummaryResponse response = monthlySpendingSummaryService.recalculateSpendingSummary(
                limitId,monthValue,yearValue
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<MonthlySpendingSummaryResponse>builder().data(response).build());
    }


}
