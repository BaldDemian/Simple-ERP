package com.nju.edu.erp.model.vo.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleDetailsCondVO {
    /**
     * 开始时间，格式：“yyyy-MM-dd”，如"2022-05-12"
     */
    private String beginDateStr;

    /**
     * 结束时间，格式：“yyyy-MM-dd”，如"2022-05-12"
     */
    private String endDateStr;

    /**
     * 商品名
     */
    private String name;

    /**
     * 客户
     */
    private Integer supplier;

    /**
     * 业务员
     */
    private String salesman;

    // todo
    /**
     * 仓库？？？
     */
}
