package com.investment_be.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.investment_be.model.portfolio.ClientPortfolio;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ClientPortfolio> portfolioItems;
}
