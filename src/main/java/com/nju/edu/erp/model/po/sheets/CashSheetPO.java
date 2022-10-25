package com.nju.edu.erp.model.po.sheets;

import com.nju.edu.erp.model.po.SheetPO;
import com.nju.edu.erp.model.vo.finance.CashSheetContentVO;
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
// 现金费用单
public class CashSheetPO extends SheetPO {
    /**
     * 银行账户
     */
    private String account;
    /**
     * 总额
     */
    private BigDecimal totalAmount;
    /**
     * 条目列表
     */
    List<CashSheetContentVO> cashSheetContents;
}
