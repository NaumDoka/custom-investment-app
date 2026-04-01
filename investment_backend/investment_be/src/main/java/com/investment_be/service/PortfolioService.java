package com.investment_be.service;

import com.investment_be.model.Asset;
import com.investment_be.model.portfolio.ClientPortfolio;
import com.investment_be.repository.AssetRepository;
import com.investment_be.repository.PortfolioRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepo;
    private final AssetRepository assetRepo;

    public List<PortfolioValue> getPortfolioForUser(Long userId){
        List<ClientPortfolio> portfolio = portfolioRepo.findByUserId(userId);

        return portfolio.stream().map(p -> {
            Asset asset = assetRepo.findByName(p.getAsset()).orElseThrow();
            double value = p.getAmount() * asset.getPrice();
            return new PortfolioValue(p.getAsset(), p.getAmount(), asset.getPrice(), value);
        }).toList();
    }

    @Data
    public static class PortfolioValue {
        private String asset;
        private double amount;
        private double currentPrice;
        private double value;

        public PortfolioValue(String asset, double amount, double currentPrice, double value){
            this.asset = asset;
            this.amount = amount;
            this.currentPrice = currentPrice;
            this.value = value;
        }
    }
}
