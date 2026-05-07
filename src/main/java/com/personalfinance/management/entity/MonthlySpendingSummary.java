package com.personalfinance.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.constant.StatusSpent;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "monthly_spending_summary",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "limit_id", "month_value", "year_value"})
        })
public class MonthlySpendingSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "summary_id",nullable = false,unique = true,length = 50,updatable = false)
    private String summaryId;

    @Column(name = "total_spent", nullable = false)
    private Long totalSpent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_spent", nullable = false,length = 50)
    private StatusSpent statusSpent;

    @Enumerated(EnumType.STRING)
    @Column(name = "month_value", nullable = false,length = 50)
    private MonthValue monthValue;

    @Column(name = "year_value", nullable = false)
    private Integer yearValue;

    @Hidden
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "user_id", nullable = false)
    private UserEntity user;

    @Hidden
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "limit_id",referencedColumnName = "limit_id", nullable = false)
    private MonthlySpendingLimit monthlySpendingLimit;
}
