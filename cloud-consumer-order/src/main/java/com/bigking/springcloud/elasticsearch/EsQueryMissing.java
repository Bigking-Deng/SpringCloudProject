package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsQueryMissing implements EsQueryBoolInterface{
    public Map<String, Object> missing = new HashMap<>();

    public EsQueryMissing setField(String fieldName){
        this.missing.put("field", fieldName);
        return this;
    }


}
