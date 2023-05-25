package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsQueryRange implements EsQueryBoolInterface{
    public Map<String, RangeCompareEntity> range = new HashMap<>();
    public EsQueryRange setRange(String field, RangeCompareEntity rangeCompareEntity){
        this.range.put(field, rangeCompareEntity);
        return this;
    }

}


