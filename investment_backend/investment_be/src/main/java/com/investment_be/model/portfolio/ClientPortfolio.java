package com.investment_be.model.portfolio;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.investment_be.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "portfolio")
public class ClientPortfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference
    private User user;

    @Column(name = "user_id")
    private Long userId;
    private String asset;
    private Double amount;
}
