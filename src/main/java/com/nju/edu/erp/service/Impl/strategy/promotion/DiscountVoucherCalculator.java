package com.nju.edu.erp.service.Impl.strategy.promotion;

import com.nju.edu.erp.model.po.SaleSheetPO;

import java.math.BigDecimal;

public class DiscountVoucherCalculator {

    public static BigDecimal calculate(SaleSheetPO saleSheetPO) {
        BigDecimal amount = saleSheetPO.getRawTotalAmount().multiply(saleSheetPO.getDiscount()).subtract(saleSheetPO.getVoucherAmount());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            // 代金券多不退
            amount = BigDecimal.ZERO;
        }
        return amount;
    }
}
