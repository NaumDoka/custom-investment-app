package com.investment_be.repository;

import com.investment_be.model.portfolio.ClientPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<ClientPortfolio, Long> {
    List<ClientPortfolio> findByUserId(Long userId);
}