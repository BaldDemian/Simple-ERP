package com.nju.edu.erp.service.Impl.strategy.promotion;

import com.nju.edu.erp.model.po.SaleSheetPO;

import java.math.BigDecimal;

public interface PromotionStrategy {

    /**
     * 前置条件：
     * saleSheetPO的其他项均已填好，且相关的contents记录已经入库
     * discount需要在promotion中设置，默认值为1
     * 代金券总额=代金券面值+其他
     * @param saleSheetPO
     * @return
     */
    void setFinalAmount(SaleSheetPO saleSheetPO);
}
