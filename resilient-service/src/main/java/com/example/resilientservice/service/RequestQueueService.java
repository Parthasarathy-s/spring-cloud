package com.example.resilientservice.service;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Slf4j
@Service
public class RequestQueueService {
    private final BlockingQueue<QueuedRequest> requestQueue;
    private final ScheduledExecutorService scheduler;

    public RequestQueueService() {
        this.requestQueue = new LinkedBlockingQueue<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        startQueueProcessor();
    }

    public CompletableFuture<String> enqueueRequest(String data, ProcessingService processingService) {
        CompletableFuture<String> future = new CompletableFuture<>();
        QueuedRequest request = new QueuedRequest(data, future, processingService);
        requestQueue.offer(request);
        log.info("Request queued: {}", data);
        return future;
    }

    private void startQueueProcessor() {
        scheduler.scheduleWithFixedDelay(this::processQueuedRequests, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void processQueuedRequests() {
        QueuedRequest request;
        while ((request = requestQueue.poll()) != null) {
            QueuedRequest currentRequest = request;
            try {
                request.getProcessingService().processAsync(request.getData())
                    .thenAccept(result -> currentRequest.getFuture().complete(result))
                    .exceptionally(ex -> {
                        if (shouldRetry(ex)) {
                            log.warn("Retrying failed request: {}", currentRequest.getData());
                            requestQueue.offer(currentRequest);
                        } else {
                            currentRequest.getFuture().completeExceptionally(ex);
                        }
                        return null;
                    });
            } catch (Exception e) {
                if (shouldRetry(e)) {
                    requestQueue.offer(currentRequest);
                } else {
                    currentRequest.getFuture().completeExceptionally(e);
                }
            }
        }
    }

    private boolean shouldRetry(Throwable ex) {
        return ex instanceof BulkheadFullException ||
               ex instanceof RejectedExecutionException;
    }

    private static class QueuedRequest {
        private final String data;
        private final CompletableFuture<String> future;
        private final ProcessingService processingService;

        public QueuedRequest(String data, CompletableFuture<String> future, ProcessingService processingService) {
            this.data = data;
            this.future = future;
            this.processingService = processingService;
        }

        public String getData() { return data; }
        public CompletableFuture<String> getFuture() { return future; }
        public ProcessingService getProcessingService() { return processingService; }
    }
}