package com.investment_be.model.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandleDto {
    private String asset;
    private String time;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
}
