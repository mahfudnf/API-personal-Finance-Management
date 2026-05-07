package com.personalfinance.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "saving_transaction")
public class SavingTransaction {
    @Id
    @Column(name = "saving_transaction_id", nullable = false,unique = true,length = 50,updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String savingTransactionId;

    @Column(name = "transaction_amount",nullable = false)
    private Long transactionAmount;

    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Hidden
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_id",referencedColumnName = "saving_id",nullable = false)
    private Saving saving;

    @Hidden
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "user_id",nullable = false)
    private UserEntity user;

    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
