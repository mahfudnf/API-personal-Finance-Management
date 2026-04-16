package com.personalfinance.management.repository;

import com.personalfinance.management.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByName(String name);
}
