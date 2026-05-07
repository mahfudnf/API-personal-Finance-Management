package com.personalfinance.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personalfinance.management.constant.MonthValue;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "monthly_spending_limit",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "month_value", "year_value"})
        })
public class MonthlySpendingLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "limit_id",nullable = false,unique = true,length = 50,updatable = false)
    private String limitId;

    @Column(name = "limit_value", nullable = false)
    private Long limitValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "month_value", nullable = false)
    private MonthValue monthValue;

    @Column(name = "year_value", nullable = false)
    private Integer yearValue;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "monthlySpendingLimit", fetch = FetchType.LAZY)
    List<MonthlySpendingSummary> monthlySpendingSummaries;

    @Hidden
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "user_id", nullable = false)
    private UserEntity user;


}
