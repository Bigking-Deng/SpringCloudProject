package com.bigking.springcloud.filter.config;

import com.bigking.springcloud.filter.InsertDataCheckGatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class InsertDataCheckGatewayFilterFactory extends AbstractGatewayFilterFactory {
    @Override
    public GatewayFilter apply(Object config) {
        return new InsertDataCheckGatewayFilter();
    }
}
