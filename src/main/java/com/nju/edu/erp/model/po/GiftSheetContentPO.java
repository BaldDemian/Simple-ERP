package com.nju.edu.erp.model.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftSheetContentPO {

    /**
     * 自增id
     */
    private Integer id;
    /**
     * 赠品单id
     */
    private String giftSheetId;
    /**
     * 商品id
     */
    private String pid;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 备注
     */
    private String remark;
}
