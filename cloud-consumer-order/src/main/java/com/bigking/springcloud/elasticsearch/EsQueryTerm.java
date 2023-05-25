package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class EsQueryTerm implements EsQueryBoolInterface{
    public Map<String, Object> term = new HashMap<>();

    public EsQueryTerm setTerm(String field, Object value){
        this.term.put(field, value);
        return this;
    }


}
