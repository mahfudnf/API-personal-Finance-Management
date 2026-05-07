package com.personalfinance.management.controller;

import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.model.response.DashboardResponse;
import com.personalfinance.management.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping(
            path = "/api/dashboard",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<DashboardResponse>> getDashboard(){
        DashboardResponse response = dashboardService.getDashboard();
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<DashboardResponse>builder().data(response).build());
    }
}
