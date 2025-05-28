# Resilient Service
A Spring Boot application demonstrating resilience patterns using Resilience4j library.

## Overview
This service implements various resilience patterns to handle failures gracefully in distributed systems. It uses Spring Boot 3.x with Java 17 and integrates Resilience4j for implementing circuit breaker, bulkhead, retry, and time limiter patterns.

## Features
- Circuit Breaker Pattern : Prevents cascading failures by breaking the circuit when error thresholds are reached
- Bulkhead Pattern : Isolates different parts of the system to contain failures
- Retry Pattern : Automatically retries failed operations with exponential backoff
- Time Limiter : Ensures operations complete within defined timeouts
- Metrics & Monitoring : Integrated with Spring Boot Actuator for health checks and metrics
## Prerequisites
- Java 17 or higher
- Maven 3.x
## Dependencies
- Spring Boot 3.1.5
- Resilience4j 2.1.0
- Spring Boot Actuator
- Lombok
## Configuration
The application includes pre-configured resilience patterns:

### Circuit Breaker
- Sliding window size: 100 requests
- Failure rate threshold: 50%
- Minimum calls before calculating error rate: 10
- Wait duration in open state: 10 seconds
### Bulkhead
- Max thread pool size: 5
- Core thread pool size: 5
- Queue capacity: 100
### Retry
- Maximum attempts: 3
- Wait duration: 1 second
- Exponential backoff enabled with multiplier of 2
### Time Limiter
- Timeout duration: 2 seconds
- Cancels running future on timeout
## Building the Application
```
mvn clean install
```
## Running the Application
```
mvn spring-boot:run
```
## Monitoring
The application exposes several actuator endpoints:

- Health check: /actuator/health
- Metrics: /actuator/metrics
- Resilience4j metrics: /actuator/resilience4j
## Testing
Execute the test suite using:

```
mvn test
```
For manual API testing, you can use the provided test-requests.sh script.

## License
This project is licensed under the MIT License.