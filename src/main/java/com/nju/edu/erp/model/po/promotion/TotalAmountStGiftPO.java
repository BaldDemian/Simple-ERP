package com.nju.edu.erp.model.po.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalAmountStGiftPO {

    private Integer id;

    private Integer stId;

    private String pid;

    private Integer quantity;
}
