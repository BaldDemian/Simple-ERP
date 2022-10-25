package com.nju.edu.erp.model.vo.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalAmountStVO {

    private Integer id;

    private BigDecimal amount;

    private BigDecimal voucher;

    Map<String, Integer> gifts;
}
