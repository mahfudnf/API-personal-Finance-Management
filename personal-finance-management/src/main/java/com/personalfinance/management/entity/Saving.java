package com.personalfinance.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "savings")
public class Saving {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "saving_id")
    private String savingId;

    @Column(name = "name_saving")
    private String nameSaving;

    @Column(name = "target_amount")
    private Long targetAmount;

    private LocalDate deadline;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "user_id")
    private UserEntity user;

    @OneToMany (mappedBy = "saving")
    List<SavingTransaction> transactions;
}
