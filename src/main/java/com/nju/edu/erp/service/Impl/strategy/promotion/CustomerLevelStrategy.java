package com.nju.edu.erp.service.Impl.strategy.promotion;

import com.nju.edu.erp.dao.CustomerDao;
import com.nju.edu.erp.dao.LevelDiscountDao;
import com.nju.edu.erp.model.po.promotion.LevelDiscountStPO;
import com.nju.edu.erp.model.po.SaleSheetPO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerLevelStrategy implements PromotionStrategy{

    private Map<Integer, BigDecimal> discountForLevel;

    private LevelDiscountDao levelDiscountDao;

    private CustomerDao customerDao;

    public CustomerLevelStrategy(LevelDiscountDao levelDiscountDao, CustomerDao customerDao) {
        this.levelDiscountDao = levelDiscountDao;
        this.customerDao = customerDao;
    }

    private void initDiscountForLevel() {
        databaseInit();
    }

    private void defaultInit() {
        discountForLevel = new HashMap<>();
        discountForLevel.put(1, BigDecimal.valueOf(1));
        discountForLevel.put(2, BigDecimal.valueOf(0.98));
        discountForLevel.put(3, BigDecimal.valueOf(0.9));
        discountForLevel.put(4, BigDecimal.valueOf(0.85));
        discountForLevel.put(5, BigDecimal.valueOf(0.8));
    }

    private void databaseInit() {
        List<LevelDiscountStPO> all = levelDiscountDao.findAll();
        discountForLevel = new HashMap<>();
        all.forEach(ld -> {
            discountForLevel.put(ld.getLevel(), ld.getDiscount());
        });
    }

    /**
     * 针对不同级别的用户的促销策略
     * 暂时只包括给予不同级别不同折扣
     * 应该允许总经理自由设置具体数值
     * 当多个折扣冲突时，取较小值
     * 最终计算方式仍然为打折后计算代金券
     * @param saleSheetPO
     * @return
     */
    @Override
    public void setFinalAmount(SaleSheetPO saleSheetPO) {
        // 以防中途总经理修改造成的不一致，每次使用该策略时都应该重新写表
        initDiscountForLevel();

        Integer level = customerDao.findOneById(saleSheetPO.getSupplier()).getLevel();
        BigDecimal discount = discountForLevel.get(level);
        if (saleSheetPO.getDiscount() != null) {
            if (saleSheetPO.getDiscount().compareTo(discount) < 0) {
                discount = saleSheetPO.getDiscount();
            }
        }
        saleSheetPO.setDiscount(discount);
        saleSheetPO.setFinalAmount(DiscountVoucherCalculator.calculate(saleSheetPO));
    }

    @Override
    public String toString() {
        return "顾客等级折扣策略";
    }
}
