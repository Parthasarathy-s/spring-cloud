package com.example.resilientservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class ExternalApiService {
    private final Random random = new Random();

    public String processRequest(String data) throws Exception {
        // Simulate external API latency
        Thread.sleep(1000);

        // Simulate 20% failure rate
        if (random.nextDouble() < 0.2) {
            log.error("External API failed for data: {}", data);
            throw new Exception("External API failed");
        }

        log.info("External API processed data: {}", data);
        return "Processed: " + data;
    }
}