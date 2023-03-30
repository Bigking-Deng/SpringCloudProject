package com.bigking.springcloud.filter.config;

import com.bigking.springcloud.filter.GlobalAuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalConfig {

    @Bean
    public GlobalAuthTokenFilter globalAuthTokenFilter(){
        return new GlobalAuthTokenFilter();
    }
}
