package com.personalfinance.management.repository;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.entity.Income;
import com.personalfinance.management.entity.MonthlySpendingLimit;
import com.personalfinance.management.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlySpendingLimitRepository extends JpaRepository<MonthlySpendingLimit,String> {

    Optional<MonthlySpendingLimit> findByUserAndMonthValueAndYearValue
            (UserEntity user, MonthValue month, Integer year);

    Optional<MonthlySpendingLimit> findByUserAndLimitId(UserEntity user,String limitId);

    Page<MonthlySpendingLimit> findByUser(UserEntity user, Pageable pageable);

    Page<MonthlySpendingLimit> findByUserAndLimitValue(
            UserEntity user, Long limitValue, Pageable pageable);

    @Query("""
    SELECT m.limitValue
    FROM MonthlySpendingLimit m
    WHERE m.user.userId = :userId
""")
    Long findLimitValueByUserId(@Param("userId") String userId);
}
