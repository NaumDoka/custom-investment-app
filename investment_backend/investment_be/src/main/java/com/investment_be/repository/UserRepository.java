package com.investment_be.repository;

import com.investment_be.model.User;
import com.investment_be.model.portfolio.UserSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    @Query("SELECT new com.investment_be.model.portfolio.UserSummaryDTO(" +
            "u.id, u.email, u.phone, u.role, " +
            "CAST(COALESCE(SUM(p.amount * a.price), 0) AS double)) " +
            "FROM User u " +
            "LEFT JOIN ClientPortfolio p ON p.userId = u.id " +
            "LEFT JOIN Asset a ON p.asset = a.name " +
            "GROUP BY u.id, u.email, u.phone, u.role")
    List<UserSummaryDTO> findAllUserSummaries();

    Optional<User> findByEmail(String email);
}
