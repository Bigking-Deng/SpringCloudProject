package com.bigking.springcloud.service;

import com.bigking.springcloud.pojo.CommonResult;
import com.bigking.springcloud.service.hytrixFallbackMethod.ConsumerServiceFallbackHandler;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(value = "microservice-payment/payment", fallback = ConsumerServiceFallbackHandler.class)
public interface FeignConsumerService {

    @GetMapping("/payment/get/{id}")
    public CommonResult<?> getPOJOResult(
            @PathVariable("id") int id,
            @RequestParam(value = "otherParam", required = false) String param
    );

    @PostMapping("/payment/insert")
    public CommonResult<?> insertDemoPOJO(
            @RequestParam("id") int id,
            @RequestParam("value") String value
    );
}
