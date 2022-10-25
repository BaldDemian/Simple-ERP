package com.nju.edu.erp.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface CardDao {
    Integer findCardNumById(Integer workerId);

    void addWorker(Integer workerId);
}
