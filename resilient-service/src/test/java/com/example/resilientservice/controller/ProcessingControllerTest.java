package com.example.resilientservice.controller;

import com.example.resilientservice.model.ProcessRequest;
import com.example.resilientservice.service.ProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessingController.class)
public class ProcessingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessingService processingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSuccessfulRequest() throws Exception {
        ProcessRequest request = new ProcessRequest();
        request.setData("test");

        when(processingService.processAsync(anyString()))
                .thenReturn(CompletableFuture.completedFuture("Success"));

        mockMvc.perform(post("/api/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testBulkheadFull() throws Exception {
        ProcessRequest request = new ProcessRequest();
        request.setData("test");

        when(processingService.processAsync(anyString()))
                .thenThrow(BulkheadFullException.class);

        mockMvc.perform(post("/api/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    public void testCircuitBreakerOpen() throws Exception {
        ProcessRequest request = new ProcessRequest();
        request.setData("test");

        when(processingService.processAsync(anyString()))
                .thenThrow(CallNotPermittedException.class);

        mockMvc.perform(post("/api/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());
    }
}