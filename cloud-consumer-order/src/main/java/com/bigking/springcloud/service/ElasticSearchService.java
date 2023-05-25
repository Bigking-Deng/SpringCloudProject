package com.bigking.springcloud.service;

import com.bigking.springcloud.pojo.SipLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface ElasticSearchService {
    Map<String, List<SipLog>> getSipLogs(List<String> callIds, String startTime, String endTime);
}
