package com.nju.edu.erp.service;


import com.nju.edu.erp.model.po.promotion.LevelDiscountStPO;

import java.math.BigDecimal;
import java.util.List;

public interface LevelDiscountStService {


    List<LevelDiscountStPO> findAll();

    void updateDiscountByLevel(Integer level, BigDecimal discount);

    LevelDiscountStPO findByLevel(Integer level);
}
