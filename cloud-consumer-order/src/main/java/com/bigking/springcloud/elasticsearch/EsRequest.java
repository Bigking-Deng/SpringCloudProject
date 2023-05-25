package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class EsRequest {
    public Integer from = 0;
    public Integer size = 3000;
    public EsQuery query;
    public List<Map<String, EsSortEntity>> sort = new ArrayList<>();
    public Map<String, EsAggInterface> aggs = new HashMap<>();

    public EsRequest addSort(String field, String order){
        Map<String, EsSortEntity> sortMap = new HashMap<>();
        sortMap.put(field, new EsSortEntity().setOrder(order));
        this.sort.add(sortMap);
        return this;
    }

    public EsRequest addAggs(String aggName, EsAggInterface aggs){
        this.aggs.put(aggName, aggs);
        return this;
    }

    public EsRequest addQueryBool(EsQuery esQuery){
        this.query = esQuery;
        return this;
    }

    public EsRequest addSize(Integer size){
        this.size = size;
        return this;
    }

    public EsRequest addFrom(Integer from){
        this.from = from;
        return this;
    }


}
