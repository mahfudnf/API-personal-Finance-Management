package com.personalfinance.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personalfinance.management.constant.Status;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "saving_id", nullable = false,unique = true,length = 50,updatable = false)
    private String savingId;

    @Column(name = "name_saving",nullable = false, length = 255)
    private String nameSaving;

    @Column(name = "target_saving", nullable = false)
    private Long targetSaving;

    @Column(name = "deadline_saving", nullable = false)
    private LocalDate deadlineSaving;

    @Column(nullable = false,length = 50)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Hidden
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "user_id",nullable = false)
    private UserEntity user;

    @Hidden
    @JsonIgnore
    @OneToMany (mappedBy = "saving",fetch = FetchType.LAZY, orphanRemoval = true)
    List<SavingTransaction> transactions;

    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = Status.PROGRESS;
    }

    @PreUpdate
    void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
