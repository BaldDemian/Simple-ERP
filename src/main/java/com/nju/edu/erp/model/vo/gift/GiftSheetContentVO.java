package com.nju.edu.erp.model.vo.gift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftSheetContentVO {

    /**
     * 自增id，传null
     */
    private Integer id;
    /**
     * 赠品单id，传null
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
