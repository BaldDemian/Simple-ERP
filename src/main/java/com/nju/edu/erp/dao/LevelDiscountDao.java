package com.nju.edu.erp.dao;

import com.nju.edu.erp.model.po.promotion.LevelDiscountStPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Mapper
public interface LevelDiscountDao {

    @Select("select * from level_discount_st")
    List<LevelDiscountStPO> findAll();

    @Update("update level_discount_st set discount=#{discount} where `level`=#{level}")
    void updateDiscountByLevel(Integer level, BigDecimal discount);

    @Select("select * from level_discount_st where `level`=#{level}")
    LevelDiscountStPO findByLevel(Integer level);
}
