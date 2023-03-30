package com.bigking.springcloud.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


@Order(2)
public class GlobalAuthTokenFilter implements GlobalFilter {

    private static List<String> tokenList = Arrays.asList("Bearer bigkingAuth", "Basic bigkingAuth");
    private static String warningMessage = "Don't have token! " + HttpStatus.UNAUTHORIZED.getReasonPhrase();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(!tokenList.contains(token)){
            ServerHttpResponse response = exchange.getResponse();
            //响应式编程，response写回数据的方法实现
            byte[] bytes = warningMessage.getBytes(StandardCharsets.UTF_8);
            DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Mono.just(dataBuffer));

//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
