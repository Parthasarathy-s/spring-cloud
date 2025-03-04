# spring-cloud-demo
Spring boot cloud boilerplate code

http://localhost:8761/
1) start naming-server (eureka) : port 8761
2) start currency-exchange(loaded from h2) :  port 8000 series
3) start currency-conversion(feign client pulls from exchange) : port 8100 series
4) start api gateway : port 8765

Api gateway predicts routes from naming-server and will hit currency-conversion or currency exchange service accordingly with load balance

