package com.personalfinance.management.repository;

import com.personalfinance.management.entity.Expense;
import com.personalfinance.management.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,String>, JpaSpecificationExecutor<Expense> {

    Optional<Expense> findByUserAndExpenseId(UserEntity user,String expenseId);

    @Query("""
       SELECT SUM(e.amount)
       FROM Expense e
       WHERE e.user.id = :userId
       """)
    Long sumByUserId(@Param("userId") String userId);
}
