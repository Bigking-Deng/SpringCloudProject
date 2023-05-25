package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EsQueryMatchPhrase implements EsQueryBoolInterface{
    public Map<String, Object> match_phrase = new HashMap<>();

    public EsQueryMatchPhrase setMatchPhrase(String field, Object value){
        this.match_phrase.put(field, value);
        return this;
    }


}
