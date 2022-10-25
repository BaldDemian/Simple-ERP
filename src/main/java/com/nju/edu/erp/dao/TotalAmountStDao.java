package com.nju.edu.erp.dao;

import com.nju.edu.erp.enums.promotion.TotalAmountStState;
import com.nju.edu.erp.model.po.promotion.TotalAmountStPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Mapper
public interface TotalAmountStDao {

    @Select("select * from total_amount_st")
    List<TotalAmountStPO> findAll();

    @Select("select * from total_amount_st where state=#{state}")
    List<TotalAmountStPO> findByState(TotalAmountStState state);

    @Update("update total_amount_st set state=#{state} where id=#{id}")
    int updateStateById(Integer id, TotalAmountStState state);

    @Insert("insert into total_amount_st(`amount`, `voucher`) values(#{amount}, #{voucher})")
    int add(BigDecimal amount, BigDecimal voucher);

    @Select("select * from total_amount_st order by id desc limit 0,1")
    TotalAmountStPO getLatest();

    @Select("select * from total_amount_st where `amount`=#{amount}")
    TotalAmountStPO findByAmount(BigDecimal amount);
}
