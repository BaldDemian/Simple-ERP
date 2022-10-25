package com.nju.edu.erp.dao;

import com.nju.edu.erp.model.po.promotion.TotalAmountStGiftPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TotalAmountStGiftDao {

    @Select("select * from total_amount_st_gift where st_id=#{stId}")
    List<TotalAmountStGiftPO> selectByStId(Integer stId);

    @Insert("insert into total_amount_st_gift(`pid`, `quantity`, `st_id`) values(#{pid}, #{quantity}, #{stId})")
    int add(String pid, Integer quantity, Integer stId);
}
