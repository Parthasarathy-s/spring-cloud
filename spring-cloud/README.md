# spring-cloud-demo
Spring boot cloud boilerplate code

1) start naming-server (eureka) : port 8761
2) start currency-exchange(loaded from h2) :  port 8000 series (run multiple ports)
3) start currency-conversion(feign client pulls from exchange) : port 8100 series
4) start api gateway : port 8765

Api gateway predicts routes from naming-server and will hit currency-conversion or currency exchange service accordingly with load balance

Reference Links:
http://localhost:8761/
http://localhost:8000/currency-exchange/from/USD/to/INR
http://localhost:8100/currency-conversion-feign/from/USD/to/INR/quantity/12
http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR