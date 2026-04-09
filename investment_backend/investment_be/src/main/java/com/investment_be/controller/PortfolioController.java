package com.investment_be.controller;

import com.investment_be.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping("/{userId}")
    public List<PortfolioService.PortfolioValue> getPortfolio(@PathVariable Long userId){
        return portfolioService.getPortfolioForUser(userId);
    }
}
