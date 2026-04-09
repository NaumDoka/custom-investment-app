package com.investment_be.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class CronJobHealthController {

    @GetMapping("/hc")
    public Map<String, String> healthCheck() {
        return Map.of("status", "ALIVE", "timestamp", String.valueOf(System.currentTimeMillis()));
    }

}
