package com.nju.edu.erp.model.vo.SaleReturns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleReturnSheetContentVO {
    /**
     * 自增id
     */
    private Integer id;
    /**
     * 销售退货单id
     */
    private String saleReturnSheetId;
    /**
     * 商品id
     */
    private String pid;
    /**
     * 退货数量（因为要支持退回部分商品的部分数量）
     */
    private Integer quantity;
    /**
     * 商品单价
     */
    private BigDecimal unitPrice;
    /**
     * 总额
     */
    private BigDecimal totalPrice;
    /**
     * 备注
     */
    private String remark;
}
