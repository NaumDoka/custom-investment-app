package com.investment_be.controller;

import com.investment_be.model.Asset;
import com.investment_be.repository.PortfolioRepository;
import com.investment_be.service.AssetService;
import com.investment_be.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AssetController {
    private final AssetService assetService;
    private final SimpMessagingTemplate ws;
    private final PortfolioRepository portfolioRepo;
    private final PortfolioService portfolioService;

    @GetMapping
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    @PostMapping("/update")
    public Asset updateAsset(@RequestParam Long id, @RequestParam Double price) {
        Asset a = assetService.updatePrice(id, price);
        ws.convertAndSend("/topic/update", a);
        portfolioRepo.findAll().forEach(p -> {
            List<PortfolioService.PortfolioValue> portfolio = portfolioService.getPortfolioForUser(p.getUserId());
            ws.convertAndSend("/topic/portfolio/" + p.getUserId(), portfolio);
        });

        return a;
    }
}
