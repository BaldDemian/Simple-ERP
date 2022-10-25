package com.nju.edu.erp.model.po;

import com.nju.edu.erp.enums.sheetState.SaleReturnSheetState;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleReturnSheetPO {
    /**
     * 销售退货单单据编号，格式为"XSTHD-yyyyMMdd-xxxxx"，新建单据时前端应该传null
     */
    private String id;
    /**
     * 关联的销售单id
     */
    private String saleSheetId;
    /**
     * 操作员
     */
    private String operator;
    /**
     * 备注
     */
    private String remark;
    /**
     * 销售退货的总额合计
     */
    private BigDecimal totalAmount;
    /**
     * 销售退货单状态
     */
    private SaleReturnSheetState state;
    /**
     * 创建时间
     */
    private Date createTime;
}
