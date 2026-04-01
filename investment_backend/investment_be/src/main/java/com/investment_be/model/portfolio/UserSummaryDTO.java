package com.investment_be.model.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String email;
    private String phone;
    private String role;
    private Double totalPortfolioValue;
}
