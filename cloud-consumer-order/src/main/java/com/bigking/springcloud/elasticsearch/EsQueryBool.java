package com.bigking.springcloud.elasticsearch;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class EsQueryBool{
    public List<EsQueryBoolInterface> must = new ArrayList<>();
    public List<EsQueryBoolInterface> must_not = new ArrayList<>();
    public List<EsQueryBoolInterface> should = new ArrayList<>();
    public List<EsQueryBoolInterface> filter = new ArrayList<>();


    public EsQueryBool addMust(EsQueryBoolInterface... esQuery){
        this.must.addAll(Arrays.asList(esQuery));
        return this;
    }

    public EsQueryBool addMustNot(EsQueryBoolInterface... esQuery){
        this.must_not.addAll(Arrays.asList(esQuery));
        return this;
    }

    public EsQueryBool addShould(EsQueryBoolInterface... esQuery){
        this.should.addAll(Arrays.asList(esQuery));
        return this;
    }

    public EsQueryBool addFilter(EsQueryBoolInterface... esQuery){
        this.filter.addAll(Arrays.asList(esQuery));
        return this;
    }

}
