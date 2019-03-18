# simple-microservice-app
Simple Applications implementing Microservices using Spring Cloud netflix oss componenets like Eureka, Ribbon, Hystrix

SimpleTradeApp is a simple trading application developed in sprinboot using monolythic architecture. This needs to be redisgned in microservices comprising of a discovery server, register service and trade service.

MSTradeAppServer is the spring boot eureka server acting as a discovery server to which multiple services need to be registered.

MSRegisterService is the spring boot eureka client which is the register microservice.

MSTradeService is the spring boot eureka client which is the trade microservice.

MSRegisterServiceRibbonHystrix is also the register microservice which has the client side load balancing implemented using ribbon and cirbuit breaker implemented using hystrix.
