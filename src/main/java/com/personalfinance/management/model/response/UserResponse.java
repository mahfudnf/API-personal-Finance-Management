package com.personalfinance.management.model.response;

import com.personalfinance.management.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
