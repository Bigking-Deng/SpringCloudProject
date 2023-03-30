package com.bigking.springcloud.dao;


import com.bigking.springcloud.pojo.DemoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PaymentDao {

    List<DemoPojo> getDemoPojo(@Param("id") int id);
    void insertDemoPojo(DemoPojo demoPojo);
}
