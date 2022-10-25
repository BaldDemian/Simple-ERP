package com.nju.edu.erp.model.vo.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessHistoryVO {

    //    商品报溢，商品报损，因无相关实现，所以未作考虑

    /**
     * 销售收入=sale-return
     */
    private BigDecimal sale;

    /**
     * 成本调价可能导致收入或支出
     */
    private BigDecimal costChange;

    /**
     * 进货退货差价可能导致收入或支出
     */
    private BigDecimal purchaseReturn;

    /**
     * 代金券支出
     */
    private BigDecimal voucher;

    /**
     * 销售成本支出=sale-return
     */
    private BigDecimal saleCost;

    /**
     * 人力成本支出
     */
    private BigDecimal laborCost;

    /**
     * 赠品成本
     */
    private BigDecimal giftCost;

    /**
     * 总收入
     */
    private BigDecimal revenue;

    /**
     * 折让额
     */
    private BigDecimal discount;

    /**
     * 折让后总收入=总收入-折让额
     */
    private BigDecimal revenueAfterDiscount;

    /**
     * 总支出
     */
    private BigDecimal spending;

    /**
     * 利润=折让后总收入-总支出
     */
    private BigDecimal profit;

}
