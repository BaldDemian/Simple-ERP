package com.nju.edu.erp.service.Impl.strategy.promotion;

import com.nju.edu.erp.model.po.SaleSheetPO;

public class DefaultPromotionStrategy implements PromotionStrategy{

    /**
     * 默认促销策略为打折后计算代金券
     * 只做一种朴素的计算
     * @param saleSheetPO
     * @return
     */
    @Override
    public void setFinalAmount(SaleSheetPO saleSheetPO) {
        saleSheetPO.setFinalAmount(DiscountVoucherCalculator.calculate(saleSheetPO));
    }

    @Override
    public String toString() {
        return "默认促销策略";
    }
}
