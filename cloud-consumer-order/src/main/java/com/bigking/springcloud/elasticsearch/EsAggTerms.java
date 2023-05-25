package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsAggTerms implements EsAggInterface{
    public Map<String, Object> terms = new HashMap<>();
    public Map<String, EsAggInterface> aggs = new HashMap<>();

    public EsAggTerms setTerms(String fieldName){
        this.terms.put("field", fieldName);
        return this;
    }

    public EsAggTerms setAggs(String aggName, EsAggInterface aggs){
        this.aggs.put(aggName, aggs);
        return this;
    }


}
