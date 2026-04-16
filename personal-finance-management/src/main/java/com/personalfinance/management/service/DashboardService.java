package com.personalfinance.management.service;

import com.personalfinance.management.model.dashboard.DashboardResponse;
import com.personalfinance.management.model.saving.SavingProgressResponse;

import java.util.List;

public interface DashboardService {

    DashboardResponse getDashboard(String email);
    List<SavingProgressResponse> getSavingProgress(String email);

}
