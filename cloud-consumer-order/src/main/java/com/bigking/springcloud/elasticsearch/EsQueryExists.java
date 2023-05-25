package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsQueryExists implements EsQueryBoolInterface{
    public Map<String, Object> exists = new HashMap<>();

    public EsQueryExists setField(String fieldName){
        this.exists.put("field", fieldName);
        return this;
    }


}
