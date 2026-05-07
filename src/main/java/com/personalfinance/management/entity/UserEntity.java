package com.personalfinance.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personalfinance.management.constant.Role;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id",nullable = false,unique = true,length = 50,updatable = false)
    private String userId;

    @Column(name = "first_name",nullable = false,length = 255)
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 255)
    private String lastName;

    @Column(nullable = false,unique = true,length = 255)
    private String email;

    @Column(nullable = false,length = 100)
    private String password;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at",nullable = false)
    private LocalDate updatedAt;

    @Column(nullable = false,length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    List<Income> incomes;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    List<Expense> expenses;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    List<Saving> savings;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    List<SavingTransaction> savingTransactions;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    List<MonthlySpendingLimit> currentMonthlySpendingLimits;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    List<MonthlySpendingSummary> spendingRecords;

    @PrePersist
    void onCreate(){
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.role = Role.USER;
    }

    @PreUpdate
    void onUpdate(){
        this.updatedAt = LocalDate.now();
        this.role = Role.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
