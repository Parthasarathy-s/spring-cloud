package com.example.resilientservice.controller;

import com.example.resilientservice.model.ProcessRequest;
import com.example.resilientservice.service.ProcessingService;
import com.example.resilientservice.service.RequestQueueService;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProcessingController {
    private final ProcessingService processingService;
    private final RequestQueueService requestQueueService;

    @PostMapping("/process")
    public ResponseEntity<String> processRequest(@RequestBody ProcessRequest request) {
        try {
            log.info("Processing request: {}", request);
            CompletableFuture<String> future = processingService.processAsync(request.getData())
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof BulkheadFullException
                        || ex.getCause() instanceof CallNotPermittedException ) {
                        // If bulkhead is full, queue the request
                        log.info("Bulkhead full, queuing request: {}", request);
                        return requestQueueService.enqueueRequest(request.getData(), processingService)
                            .join();
                    }
                    throw new CompletionException(ex);
                });

            // Wait for the result with a longer timeout since we might be queued
            String result = future.get(30, TimeUnit.SECONDS);
            log.info("Request completed successfully: {}", request);
            return ResponseEntity.ok(result);

        } catch (TimeoutException e) {
            log.warn("Request timed out: {}", request);
            return ResponseEntity.status(408).body("Request timeout - Operation took too long");

        } catch (InterruptedException e) {
            log.error("Request processing interrupted: {}", request, e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Request processing interrupted");

        } catch (Exception e) {
            log.error("Error processing request: {}", request, e);
            return ResponseEntity.status(500).body("Internal server error - Please try again");
        }
    }
}