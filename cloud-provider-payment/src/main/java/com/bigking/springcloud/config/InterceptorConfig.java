//package com.bigking.springcloud.config;
//
//import com.webex.mats.fraud.interceptor.AuthenticationInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @Author devwang
// * @Date 2019-06-18 15:49
// **/
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//
//    @Bean
//    AuthenticationInterceptor authenticationInterceptor() {
//        return new AuthenticationInterceptor();
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/api/**")
//                .excludePathPatterns("/healthcheck")
//                .excludePathPatterns("/api/v1/fraud/configuration/knowledgebases/**")
//                .excludePathPatterns("/api/v1/fraud/containment/ticket/attachment/download")
//                .excludePathPatterns("/api/v1/fraud/meeting/cdr/participants/export/cdr")
//                .excludePathPatterns("/api/v2/fraud/metrics/kpi/grafana/**");
//
//    }
//}
