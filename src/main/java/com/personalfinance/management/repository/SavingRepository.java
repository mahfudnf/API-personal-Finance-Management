package com.personalfinance.management.repository;

import com.personalfinance.management.entity.Income;
import com.personalfinance.management.entity.Saving;
import com.personalfinance.management.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingRepository extends JpaRepository<Saving,String>, JpaSpecificationExecutor<Saving> {

    Optional<Saving> findByUserAndSavingId(UserEntity user,String savingId);

    List<Saving> findByUser(UserEntity user);

    Page<Saving> findByUser(UserEntity user, Pageable pageable);

    Page<Saving> findByUserAndNameSavingContaining(
            UserEntity user, String category, Pageable pageable);

}
