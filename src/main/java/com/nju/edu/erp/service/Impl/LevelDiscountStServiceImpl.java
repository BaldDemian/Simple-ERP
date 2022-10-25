package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.LevelDiscountDao;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.model.po.promotion.LevelDiscountStPO;
import com.nju.edu.erp.service.LevelDiscountStService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LevelDiscountStServiceImpl implements LevelDiscountStService {

    @Autowired
    private LevelDiscountDao levelDiscountDao;

    @Override
    public List<LevelDiscountStPO> findAll() {
        return levelDiscountDao.findAll();
    }

    @Override
    @Transactional
    public void updateDiscountByLevel(Integer level, BigDecimal discount) {
        if (!correctLevel(level) || !correctDiscount(discount)) {
            throw new MyServiceException("00001", "错误的参数");
        }
        levelDiscountDao.updateDiscountByLevel(level, discount);
    }

    private boolean correctDiscount(BigDecimal discount) {
        return discount.compareTo(BigDecimal.ZERO)>0 && discount.compareTo(BigDecimal.ONE)<=0;
    }

    private boolean correctLevel(Integer level) {
        return level>=1 && level<=5;
    }

    @Override
    public LevelDiscountStPO findByLevel(Integer level) {
        if (!correctLevel(level)) {
            throw new MyServiceException("00001", "错误的参数");
        }
        return levelDiscountDao.findByLevel(level);
    }
}
