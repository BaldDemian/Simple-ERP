package com.nju.edu.erp.service;

import com.nju.edu.erp.enums.promotion.StrategyType;

public interface PromotionStrategyService {

    void chooseStrategy(StrategyType type);
}
