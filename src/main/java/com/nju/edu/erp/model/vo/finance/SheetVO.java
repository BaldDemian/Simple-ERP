package com.nju.edu.erp.model.vo.finance;

import com.nju.edu.erp.enums.sheetState.SheetState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

// 抽出各个单据类的公共字段
public class SheetVO {
    /**
     * ID
     */
    private String id;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 单据状态
     */
    private SheetState state;

    /**
     * 创建时间
     */
    private Date createDate;
}
