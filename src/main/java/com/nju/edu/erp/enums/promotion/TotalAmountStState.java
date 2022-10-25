package com.nju.edu.erp.enums.promotion;

import com.nju.edu.erp.enums.BaseEnum;

public enum TotalAmountStState implements BaseEnum<TotalAmountStState, String> {
    AVAILABLE("可用"),
    UNAVAILABLE("禁用");

    private final String value;

    TotalAmountStState(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
