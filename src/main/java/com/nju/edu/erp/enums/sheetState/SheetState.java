package com.nju.edu.erp.enums.sheetState;

import com.nju.edu.erp.enums.BaseEnum;

public enum SheetState implements BaseEnum<SheetState, String> {
    PENDING("待总经理审批"), // 财务类单据仅用总经理审批
    SUCCESS("审批完成"),
    FAILURE("审批失败");

    private final String value;

    SheetState(String value) {
        this.value = value;
    }
    @Override
    public String getValue() {
        return value;
    }
}
