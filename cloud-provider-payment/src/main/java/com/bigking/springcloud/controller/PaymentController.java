package com.bigking.springcloud.controller;


import com.bigking.springcloud.pojo.CommonResult;
import com.bigking.springcloud.pojo.DemoPojo;
import com.bigking.springcloud.pojo.ResponseStatus;
import com.bigking.springcloud.service.PaymentService;
import com.bigking.springcloud.utils.EnvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Value("${server.port}")
    String serverPort;


    @GetMapping("/payment/get/{id}")
    public CommonResult<?> getPOJOResult(
            @PathVariable("id") int id,
            @RequestParam(value = "otherParam", required = false) String param
    ){
        try{
            List<DemoPojo> list = paymentService.getDemoPojo(id);
            return new CommonResult<>(ResponseStatus.success.getCode(), list, ResponseStatus.success.getMessage()+ " IP-Port: " + EnvUtils.getIPAddress()+" "+serverPort);
        }catch (Exception e){
            log.error(e.getMessage());
            return new CommonResult<>(ResponseStatus.failure.getCode(),  ResponseStatus.failure.getMessage());
        }
    }

    @PostMapping("/payment/insert")
    public CommonResult<?> insertDemoPOJO(
            @RequestParam("id") int id,
            @RequestParam("value") String value
    ){
        try {
            DemoPojo demoPojo = new DemoPojo(id, value);
            paymentService.insertDemoPojo(demoPojo);
            return new CommonResult<>(ResponseStatus.success.getCode(),  ResponseStatus.success.getMessage()+EnvUtils.getIPAddress()+" "+serverPort);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new CommonResult<>(ResponseStatus.failure.getCode(),  ResponseStatus.failure.getMessage());
        }
    }
}
