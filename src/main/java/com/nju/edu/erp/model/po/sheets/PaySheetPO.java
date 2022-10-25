package com.nju.edu.erp.model.po.sheets;

import com.nju.edu.erp.model.po.SheetPO;
import com.nju.edu.erp.model.vo.finance.TransferContentVO;
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
public class PaySheetPO extends SheetPO {
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
