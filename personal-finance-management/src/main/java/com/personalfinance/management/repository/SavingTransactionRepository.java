package com.personalfinance.management.repository;

import com.personalfinance.management.entity.SavingTransaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingTransactionRepository extends JpaRepository<SavingTransaction,String>, JpaSpecificationExecutor<SavingTransaction> {

    @Query("""
       SELECT SUM(st.amount) 
       FROM SavingTransaction st 
       WHERE st.saving.savingId = :savingId
       """)
    Long sumBySavingId(@Param("savingId") String savingId);
}
