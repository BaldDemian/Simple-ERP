package com.nju.edu.erp.model.vo.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectSheetVO extends SheetVO{
    /**
     * 总额汇总
     */
    private BigDecimal totalAmount;

    /**
     * 客户
     */
    private String customer;

    /**
     * 转账列表
     */
    private List<TransferContentVO> transferContents;
}
