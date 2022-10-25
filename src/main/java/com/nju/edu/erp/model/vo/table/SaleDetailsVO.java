package com.nju.edu.erp.model.vo.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleDetailsVO {

    /**
     * 时间（精确到天）
     */
    private String date;

    /**
     * 商品名
     */
    private String name;

    /**
     * 型号
     */
    private String type;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 总额
     */
    private BigDecimal totalPrice;
}
