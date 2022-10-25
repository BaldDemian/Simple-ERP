package com.nju.edu.erp.enums.sheetState;

import com.nju.edu.erp.enums.BaseEnum;

public enum VoucherState implements BaseEnum<VoucherState, String> {
    AVAILABLE("可用"),
    UNAVAILABLE("不可用"),
    USED("已使用");

    private final String value;

    VoucherState(String value) {
        this.value = value;
    }
    @Override
    public String getValue() {
        return value;
    }
}
