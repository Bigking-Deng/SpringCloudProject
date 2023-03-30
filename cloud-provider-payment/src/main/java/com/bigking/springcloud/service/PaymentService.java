package com.bigking.springcloud.service;

import com.bigking.springcloud.pojo.DemoPojo;

import java.util.List;


public interface PaymentService {
    List<DemoPojo> getDemoPojo(int id);
    void insertDemoPojo(DemoPojo demoPojo);
}
