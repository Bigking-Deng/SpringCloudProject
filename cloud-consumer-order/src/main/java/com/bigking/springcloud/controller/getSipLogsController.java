package com.bigking.springcloud.controller;


import com.bigking.springcloud.pojo.CommonResult;
import com.bigking.springcloud.pojo.ResponseStatus;
import com.bigking.springcloud.pojo.SipLog;
import com.bigking.springcloud.service.ConsumerService;
import com.bigking.springcloud.service.ElasticSearchService;
import com.bigking.springcloud.service.FeignConsumerService;
import com.bigking.springcloud.utils.EnvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class getSipLogsController {

    @Autowired
    ElasticSearchService elasticSearchService;


    @PostMapping("/getSipLogs")
    public CommonResult<?> getSipLogs(
            @RequestParam(value = "startTime")String startTime,
            @RequestParam(value = "endTime")String endTime,
            @RequestBody List<String> callIds
    ){
        Map<String, List<SipLog>> map = elasticSearchService.getSipLogs(callIds, startTime, endTime);
        CommonResult<?> data = new CommonResult<>(ResponseStatus.success.getCode(), map, "");
        return data;
    }


}
