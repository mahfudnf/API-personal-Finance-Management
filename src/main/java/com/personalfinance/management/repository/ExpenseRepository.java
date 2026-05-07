package com.personalfinance.management.repository;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends
        JpaRepository<Expense,String>, JpaSpecificationExecutor<Expense> {

    Optional<Expense> findByUserAndExpenseId(UserEntity user,String expenseId);

    Page<Expense> findByUser(UserEntity user, Pageable pageable);

    Page<Expense> findByUserAndCategoryContaining(
            UserEntity user, String category, Pageable pageable);

    @Query("""
       SELECT SUM(e.amount)
       FROM Expense e
       WHERE e.user.id = :userId
       """)
    Long sumByUserId(@Param("userId") String userId);

    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expense e
    WHERE e.user.userId = :userId
      AND FUNCTION('MONTH', e.createdAt) = :monthValue
      AND FUNCTION('YEAR', e.createdAt) = :yearValue
""")
    Long sumByUserIdAndMonthValueAndYearValue(
            @Param("userId") String userId,
            @Param("monthValue") Integer month,
            @Param("yearValue") Integer year
    );
}
