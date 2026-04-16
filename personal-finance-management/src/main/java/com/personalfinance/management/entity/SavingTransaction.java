package com.personalfinance.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "saving_transaction")
public class SavingTransaction {

    @Id
    @Column(name = "saving_transaction_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String savingTransactionId;

    private Long amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "saving_id",referencedColumnName = "saving_id")
    private Saving saving;
}
