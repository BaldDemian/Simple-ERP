package com.nju.edu.erp.service.Impl.strategy.promotion;

import com.nju.edu.erp.model.po.SaleSheetPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromotionContext {

    private PromotionStrategy strategy;

    @Autowired
    public PromotionContext() {
        strategy = new DefaultPromotionStrategy();
    }

    public void setStrategy(PromotionStrategy strategy) {
        if (strategy == null) {
            return;
        }
        this.strategy = strategy;
    }

    public void setFinalAmount(SaleSheetPO saleSheetPO) {
        strategy.setFinalAmount(saleSheetPO);
    }

    public String getStrategyName() {
        return strategy.toString();
    }
}
