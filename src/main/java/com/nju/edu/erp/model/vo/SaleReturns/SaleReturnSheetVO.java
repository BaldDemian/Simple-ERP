package com.nju.edu.erp.model.vo.SaleReturns;

import com.nju.edu.erp.enums.sheetState.SaleReturnSheetState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleReturnSheetVO {
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
    /**
     * 销售退货单具体内容
     */
    List<SaleReturnSheetContentVO> saleReturnSheetContent;
}
