package com.nju.edu.erp.model.po.promotion;

import com.nju.edu.erp.enums.promotion.TotalAmountStState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalAmountStPO {

    private Integer id;

    private BigDecimal amount;

    private BigDecimal voucher;

    private TotalAmountStState state;
}
