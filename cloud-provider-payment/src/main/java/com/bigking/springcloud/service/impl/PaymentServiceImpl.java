package com.bigking.springcloud.service.impl;

import com.bigking.springcloud.dao.PaymentDao;
import com.bigking.springcloud.pojo.DemoPojo;
import com.bigking.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentDao paymentDao;

    @Override
    public List<DemoPojo> getDemoPojo(int id) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return paymentDao.getDemoPojo(id);
    }

    @Override
    public void insertDemoPojo(DemoPojo demoPojo) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        paymentDao.insertDemoPojo(demoPojo);
    }
}
