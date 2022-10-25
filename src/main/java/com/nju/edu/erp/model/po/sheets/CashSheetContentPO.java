package com.nju.edu.erp.model.po.sheets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 现金费用单中的条目
public class CashSheetContentPO {
    /**
     * 关联的现金费用单的ID
     */
    private String id;
    /**
     * 条目名
     */
    private String name;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;
}
