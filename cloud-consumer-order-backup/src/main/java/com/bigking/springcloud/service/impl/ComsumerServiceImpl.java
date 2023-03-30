package com.bigking.springcloud.service.impl;

import com.bigking.springcloud.service.ConsumerService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

@Service
@DefaultProperties(defaultFallback = "globalFallbackHandler")
public class ComsumerServiceImpl implements ConsumerService {


    @HystrixCommand(fallbackMethod = "getLocalDataFallbackHandler",
                    commandProperties = {
                            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
                            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
                            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
                            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
                            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸

                    })
//    @HystrixCommand
    @Override
    public String getLocalDataDemo(int id) {
        if(id<0){
            throw new RuntimeException();
        }
        return "Get data ID success: " + id;
    }

    @HystrixCommand(fallbackMethod = "getLocalDataDemoWithWaitingHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸
    })
//    @HystrixCommand
    @Override
    public String getLocalDataDemoWithWaiting(int id) {
        try {
            Thread.sleep(1000);
            if(id<0){
                throw new RuntimeException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Get data ID success: " + id;
    }



    public String getLocalDataFallbackHandler(int id){
        return "sorry, the method1 " + id + " can't be visited";
    }

    public String getLocalDataDemoWithWaitingHandler(int id){
        return "sorry, the method2 " + id + " can't be visited";
    }

    public String globalFallbackHandler(){
        return "sorry, the method3 " + " can't be visited";
    }
}
