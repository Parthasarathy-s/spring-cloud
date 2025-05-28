package com.example.resilientservice.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingService {
    private final ExternalApiService externalApiService;

    @Bulkhead(name = "externalApi", type = Bulkhead.Type.THREADPOOL)
    @CircuitBreaker(name = "externalApi")
    @Retry(name = "externalApi")
    @TimeLimiter(name = "externalApi")
    public CompletableFuture<String> processAsync(String data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return externalApiService.processRequest(data);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process request", e);
            }
        });
    }
}