package org.example.scgapp.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RouteLocatorConfig {
    
    @Resource
    public GatewayFilter byteModifyRequestFilter;
    @Resource
    public GatewayFilter stringModifyRequestFilter;
    
    /**
     * 1. when "error" route is using stringModifyRequestFilter, "hello" route will get IllegalReferenceCountException
     * 2. when "error" route is using byteModifyRequestFilter, "hello" route will get normal response
     */
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // this provider will return immediately
                .route("hello", r -> r.path("/hello")
                        .filters(f -> f.filter(byteModifyRequestFilter))
                        .uri("http://localhost:8081"))
                // this provider will sleep 1s then return, will reach response-timeout threshold(500ms) 
                .route("error", r -> r.path("/err")
                        .filters(f -> f.filter(stringModifyRequestFilter))
//                        .filters(f -> f.filter(byteModifyRequestFilter))
                        .uri("http://localhost:8082"))
                .build();
    }
}