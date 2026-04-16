package com.personalfinance.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    private String name;

    @Column(unique = true,nullable = false)
    private String email;

    private String password;

    @OneToMany(mappedBy = "user")
    List<Income> incomes;

    @OneToMany(mappedBy = "user")
    List<Expense> expenses;

    @OneToMany(mappedBy = "user")
    List<Saving> savings;
}
