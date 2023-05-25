package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsAggAvg implements EsAggInterface{
    public Map<String, Object> avg = new HashMap<>();

    public EsAggAvg setTerm(String fieldName){
        this.avg.put("field", fieldName);
        return this;
    }


}
