package com.investment_be.service;

import com.investment_be.model.Asset;
import com.investment_be.model.market.Candle;
import com.investment_be.model.market.CandleDto;
import com.investment_be.model.portfolio.ClientPortfolio;
import com.investment_be.repository.AssetRepository;
import com.investment_be.repository.CandleRepository;
import com.investment_be.repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MarketSimulatorService {
    private final SimpMessagingTemplate messagingTemplate;
    private final AssetRepository assetRepository;
    private final CandleRepository candleRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;

    private record MarketBias(double multiplier, int remainingTicks) {}
    private final Map<String, MarketBias> activeBiases = new ConcurrentHashMap<>();
    private final Map<String, CandleDto> currentMinuteCandles = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void generateCandlesForAllAssets() {
        List<Asset> allAssets = assetRepository.findAll();
        List<ClientPortfolio> allPortfolios = portfolioRepository.findAll();
        List<Candle> candlesToPersist = new ArrayList<>();
        Instant now = Instant.now();
        Instant minuteBucket = now.truncatedTo(ChronoUnit.MINUTES);

        for (Asset asset : allAssets) {
            double openPrice = asset.getPrice();
            double move;
            double tickVolume;

            // 1. Calculate Price Movement
            MarketBias bias = activeBiases.get(asset.getName());
            if (bias != null && bias.remainingTicks > 0) {
                // "Buying/Selling Pressure" Logic
                move = openPrice * bias.multiplier;
                tickVolume = ThreadLocalRandom.current().nextDouble(8000, 15000); // Massive volume spike

                // Update bias state
                if (bias.remainingTicks <= 1) {
                    activeBiases.remove(asset.getName());
                } else {
                    activeBiases.put(asset.getName(), new MarketBias(bias.multiplier, bias.remainingTicks - 1));
                }
            } else {
                // Normal Random Walk
                double volatility = openPrice * 0.002;
                move = ThreadLocalRandom.current().nextDouble(-volatility, volatility);
                tickVolume = ThreadLocalRandom.current().nextDouble(500, 2000);
            }

            double newPrice = openPrice + move;
            asset.setPrice(newPrice);

            // 2. 1-Minute Candle Accumulation Logic
            CandleDto currentCandle = currentMinuteCandles.get(asset.getName());

            // If a new minute starts, persist the old one and start fresh
            if (currentCandle == null || !Instant.parse(currentCandle.getTime()).equals(minuteBucket)) {
                if (currentCandle != null) {
                    candlesToPersist.add(convertToEntity(asset.getName(), currentCandle));
                }
                currentCandle = new CandleDto(asset.getName(), minuteBucket.toString(),
                        openPrice, Math.max(openPrice, newPrice), Math.min(openPrice, newPrice), newPrice, tickVolume);
            }

            // Update OHLCV values for the current bar
            currentCandle.setHigh(Math.max(currentCandle.getHigh(), newPrice));
            currentCandle.setLow(Math.min(currentCandle.getLow(), newPrice));
            currentCandle.setClose(newPrice);
            currentCandle.setVolume(currentCandle.getVolume() + tickVolume);

            currentMinuteCandles.put(asset.getName(), currentCandle);

            // 3. Broadcast
            messagingTemplate.convertAndSend("/topic/candles/" + asset.getName(), currentCandle);
            messagingTemplate.convertAndSend("/topic/update", asset);
        }

        assetRepository.saveAll(allAssets);
        if (!candlesToPersist.isEmpty()) {
            candleRepository.saveAll(candlesToPersist); // One query for all candles
        }

    }

    private Candle convertToEntity(String assetName, CandleDto dto) {
        return new Candle(null, assetName, Instant.parse(dto.getTime()),
                dto.getOpen(), dto.getHigh(), dto.getLow(), dto.getClose(), dto.getVolume());
    }


    public void forceMoveSingle(String assetName, double multiplier) {
        activeBiases.put(assetName, new MarketBias(multiplier, 6));
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void cleanUpOldCandles() {
        Instant sixHoursAgo = Instant.now().minus(6, ChronoUnit.HOURS);
        candleRepository.deleteByTimestampBefore(sixHoursAgo);
    }
}
