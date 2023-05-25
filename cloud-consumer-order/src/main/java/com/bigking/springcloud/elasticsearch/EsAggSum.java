package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsAggSum implements EsAggInterface{
    public Map<String, Object> sum = new HashMap<>();

    public EsAggSum setTerm(String fieldName){
        this.sum.put("field", fieldName);
        return this;
    }


}
