package com.investment_be.service;

import com.investment_be.model.Asset;
import com.investment_be.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {
    private final AssetRepository assetRepo;

    public List<Asset> getAllAssets() {
        return assetRepo.findAll();
    }

    public Asset updatePrice(Long id, Double price) {
        Asset a = assetRepo.findById(id).orElseThrow();
        a.setPrice(price);
        return assetRepo.save(a);
    }
}
