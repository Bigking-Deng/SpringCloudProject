package com.bigking.springcloud.elasticsearch;

public class EsSortEntity {
    public String order="desc";

    //"desc", "asc"
    public EsSortEntity setOrder(String order){
        this.order=order;
        return this;
    }

}
