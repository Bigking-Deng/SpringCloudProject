package com.bigking.springcloud.controller;


import com.bigking.springcloud.pojo.CommonResult;
import com.bigking.springcloud.pojo.ResponseStatus;
import com.bigking.springcloud.service.ConsumerService;
import com.bigking.springcloud.service.FeignConsumerService;
import com.bigking.springcloud.utils.EnvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class ConsumerController {

    @Autowired
    ConsumerService consumerService;

    @Autowired
    FeignConsumerService feignConsumerService;

    @Value("${server.port}")
    String serverPort;

    @GetMapping("/consumer/get/{id}")
    public CommonResult<?> getPayment(
            @PathVariable("id") int id,
            @RequestParam(value = "otherParam", required = false) String param
    ){
        CommonResult<?> data = feignConsumerService.getPOJOResult(id, param);
        data.setMessage(" IP-Port: " + EnvUtils.getIPAddress() + ":" + serverPort);
        return data;
    }

    @PostMapping("/consumer/insertToPayment")
    public CommonResult<?> insertToPayment(
            @RequestParam("id") int id,
            @RequestParam("value") String value
    ){
        CommonResult<?> data = feignConsumerService.insertDemoPOJO(id, value);
        return data;
    }

    @GetMapping("/consumer/getLocalDataDemo/{id}")
    public CommonResult<?> getLocalDataDemo(
            @PathVariable("id") int id,
            @RequestParam(value = "otherParam", required = false) String param){
        String dataStr = consumerService.getLocalDataDemo(id);
        return new CommonResult<>(ResponseStatus.success.getCode(), dataStr, ResponseStatus.success.getMessage());
    }

    @GetMapping("/consumer/getLocalDataDemoWithWaiting/{id}")
    public CommonResult<?> getLocalDataDemoWithWaiting(
            @PathVariable("id") int id,
            @RequestParam(value = "otherParam", required = false) String param){
        String dataStr = consumerService.getLocalDataDemoWithWaiting(id);
        return new CommonResult<>(ResponseStatus.success.getCode(), dataStr, ResponseStatus.success.getMessage());
    }
}
