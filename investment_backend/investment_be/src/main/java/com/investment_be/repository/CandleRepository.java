package com.investment_be.repository;

import com.investment_be.model.market.Candle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CandleRepository extends JpaRepository<Candle, Long> {

    List<Candle> findTop50ByAssetNameOrderByTimestampDesc(String assetName);

    void deleteByTimestampBefore(Instant expiry);

}
