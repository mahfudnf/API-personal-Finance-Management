package com.personalfinance.management.repository;

import com.personalfinance.management.entity.Income;
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
public interface IncomeRepository extends JpaRepository<Income,String>, JpaSpecificationExecutor<Income> {

    Optional<Income> findByUserAndIncomeId(UserEntity user, String incomeId);

    @Query("""
       SELECT SUM(i.amount)
       FROM Income i
       WHERE i.user.id = :userId
       """)
    Long sumByUserId(@Param("userId") String userId);
}
