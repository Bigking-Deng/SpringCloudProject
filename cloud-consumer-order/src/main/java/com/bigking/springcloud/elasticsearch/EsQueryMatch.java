package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsQueryMatch {
    public Map<String, Object> match = new HashMap<>();

    public EsQueryMatch setMatch(String field, Object value){
        this.match.put(field, value);
        return this;
    }

}
