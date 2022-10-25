package com.nju.edu.erp.model.vo.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 转账列表中的每一项
public class TransferContentVO {
    /**
     * 对应的收款单/付款单编号
     */
    private String id;
    /**
     * 银行账户
     */
    private String account;
    /**
     * 转账金额
     */
    private BigDecimal transferAmount;
    /**
     * 备注
     */
    private String remark;
}
