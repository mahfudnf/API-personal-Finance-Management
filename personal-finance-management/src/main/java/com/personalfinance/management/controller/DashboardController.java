package com.personalfinance.management.controller;

import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.dashboard.DashboardResponse;
import com.personalfinance.management.model.saving.SavingProgressResponse;
import com.personalfinance.management.service.DashboardService;
import com.personalfinance.management.service.impl.DashboardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping(
            path = "/api/dashboard",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DashboardResponse> getDashboard(@AuthenticationPrincipal User user){
        DashboardResponse response = dashboardService.getDashboard(user.getUsername());
        return WebResponse.<DashboardResponse>builder().data(response).build();
    }

    @GetMapping("/api/dashboard/savings/saving_transaction/progress")
    public WebResponse<List<SavingProgressResponse>> getSavingProgress(
            @AuthenticationPrincipal User user) {

        return WebResponse.<List<SavingProgressResponse>>builder()
                .data(dashboardService.getSavingProgress(user.getUsername()))
                .build();
    }
}
