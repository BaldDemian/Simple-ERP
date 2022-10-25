package com.nju.edu.erp.model.vo.gift;

import com.nju.edu.erp.enums.sheetState.GiftSheetState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftSheetVO {
    /**
     * 赠品单单据编号（格式为：JPD-yyyyMMdd-xxxxx
     * 传null
     */
    private String id;
    /**
     * 关联的销售单id
     */
    private String saleSheetId;
    /**
     * 客户id
     */
    private Integer supplier;
    /**
     * 业务员
     */
    private String salesman;
    /**
     * 操作员
     * 传null
     */
    private String operator;
    /**
     * 备注
     */
    private String remark;
    /**
     * 单据状态，传null
     */
    private GiftSheetState state;
    /**
     * 创建时间，传null
     */
    private Date createTime;
    /**
     * 礼品内容
     */
    List<GiftSheetContentVO> giftSheetContents;
}
