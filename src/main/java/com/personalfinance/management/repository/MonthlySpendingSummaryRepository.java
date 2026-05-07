package com.personalfinance.management.repository;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.entity.MonthlySpendingLimit;
import com.personalfinance.management.entity.MonthlySpendingSummary;
import com.personalfinance.management.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlySpendingSummaryRepository extends
        JpaRepository<MonthlySpendingSummary,String> , JpaSpecificationExecutor<MonthlySpendingSummary> {

    Optional<MonthlySpendingSummary> findByUserAndMonthlySpendingLimit(UserEntity user, MonthlySpendingLimit limit);

    Optional<MonthlySpendingSummary> findByUserAndMonthValueAndYearValue(UserEntity user, MonthValue monthValue,Integer yearValue);

    Optional<MonthlySpendingSummary> findByUserAndMonthlySpendingLimitAndMonthValueAndYearValue(
            UserEntity user, MonthlySpendingLimit limit, MonthValue monthValue, Integer yearValue);
}
