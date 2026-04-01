package com.investment_be.controller;

import com.investment_be.model.market.CandleDto;
import com.investment_be.repository.CandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/market")
public class MarketSimulatorController {
    private final CandleRepository candleRepository;

    @GetMapping("/history/{assetName}")
    public ResponseEntity<List<CandleDto>> getAssetHistory(@PathVariable String assetName) {
        List<CandleDto> history = candleRepository.findTop50ByAssetNameOrderByTimestampDesc(assetName)
                .stream()
                .map(c -> new CandleDto(c.getAssetName(), c.getTimestamp().toString(), c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), c.getVolume()))
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(history);
        return ResponseEntity.ok(history);
    }
}
