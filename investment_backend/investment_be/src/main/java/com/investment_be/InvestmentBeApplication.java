package com.investment_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvestmentBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestmentBeApplication.class, args);
    }

}
