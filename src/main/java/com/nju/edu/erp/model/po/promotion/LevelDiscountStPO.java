package com.nju.edu.erp.model.po.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevelDiscountStPO {

    private Integer id;

    private Integer level;

    private BigDecimal discount;
}
