package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class EsQueryTerms implements EsQueryBoolInterface{

    public Map<String, List<Object>> terms = new HashMap<>();

    public EsQueryTerms setTerms(String field, List<Object> value){
        this.terms.put(field, value);
        return this;
    }


}
