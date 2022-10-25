package com.nju.edu.erp.dao;

import com.nju.edu.erp.enums.CustomerType;
import com.nju.edu.erp.model.po.CustomerPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Mapper
public interface CustomerDao {
    int updateOne(CustomerPO customerPO);

    CustomerPO findOneById(Integer supplier);

    List<CustomerPO> findAllByType(CustomerType customerType);

    void createCustomer (CustomerPO customerPO);

    int deleteById (Integer id);

    void updatePayable(String name, BigDecimal amount);
    void updateReceivable(String name, BigDecimal amount);

    CustomerPO findOneByName(String name);
}
