package org.example.scgapp.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class CustomModifyResponseBodyGatewayFilter {
    
    @Bean
    public GatewayFilter byteModifyRequestFilter(ModifyRequestBodyGatewayFilterFactory factory) {
        return factory.apply(
                new ModifyRequestBodyGatewayFilterFactory.Config()
                        .setRewriteFunction(byte[].class, byte[].class, (exchange, body) -> Mono.just(body))
        );
    }
    
    @Bean
    public GatewayFilter stringModifyRequestFilter(ModifyRequestBodyGatewayFilterFactory factory) {
        return factory.apply(
                new ModifyRequestBodyGatewayFilterFactory.Config()
                        .setRewriteFunction(String.class, String.class, (exchange, body) -> Mono.just(body))
        );
    }
}
