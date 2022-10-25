package com.nju.edu.erp.model.po;

import com.nju.edu.erp.enums.sheetState.VoucherState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherPO {

    private Integer id;

    /**
     * 持有者的id
     */
    private Integer customerId;

    /**
     * 面值
     */
    private BigDecimal value;

    /**
     * 状态
     */
    VoucherState state;

    /**
     * 备注
     */
    private String remark;
}
