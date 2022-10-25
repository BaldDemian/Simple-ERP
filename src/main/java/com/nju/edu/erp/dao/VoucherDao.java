package com.nju.edu.erp.dao;

import com.nju.edu.erp.model.po.VoucherPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface VoucherDao {

    List<VoucherPO> getAllByCustomerId(Integer customerId);

    List<VoucherPO> getAvailByCustomerId(Integer customerId);

    void add(VoucherPO voucherPO);

    void use(Integer id);

    VoucherPO getLatestAvailByCustomerId(Integer id);
}
