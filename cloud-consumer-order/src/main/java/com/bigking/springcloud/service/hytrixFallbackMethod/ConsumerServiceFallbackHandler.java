package com.bigking.springcloud.service.hytrixFallbackMethod;

import com.bigking.springcloud.pojo.CommonResult;
import com.bigking.springcloud.pojo.ResponseStatus;
import com.bigking.springcloud.service.FeignConsumerService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Component;

@Component
public class ConsumerServiceFallbackHandler implements FeignConsumerService {

    @Override
    public CommonResult<?> getPOJOResult(int id, String param) {
        String errMessage =  "sorry, the method getPOJOResult " + id + " can't be visited";
        return new CommonResult<>(ResponseStatus.fallback.getCode(), errMessage, ResponseStatus.fallback.getMessage());
    }

    @Override
    public CommonResult<?> insertDemoPOJO(int id, String value) {
        String errMessage =  "sorry, the method insertDemoPOJO " + id + " can't be visited";
        return new CommonResult<>(ResponseStatus.fallback.getCode(), errMessage, ResponseStatus.fallback.getMessage());
    }
}
