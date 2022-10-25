package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.*;
import com.nju.edu.erp.enums.promotion.StrategyType;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.service.GiftService;
import com.nju.edu.erp.service.Impl.strategy.promotion.*;
import com.nju.edu.erp.service.PromotionStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PromotionStrategyServiceImpl implements PromotionStrategyService {

    private final LevelDiscountDao levelDiscountDao;
    private final CustomerDao customerDao;
    private final TotalAmountStDao totalAmountStDao;
    private final VoucherDao voucherDao;
    private final TotalAmountStGiftDao totalAmountStGiftDao;
    private final GiftService giftService;
    private PromotionContext promotionContext;

    private Map<StrategyType, PromotionStrategy> strategyMap;

    @Autowired
    public PromotionStrategyServiceImpl(LevelDiscountDao levelDiscountDao, CustomerDao customerDao, TotalAmountStDao totalAmountStDao, VoucherDao voucherDao, TotalAmountStGiftDao totalAmountStGiftDao, GiftService giftService, PromotionContext promotionContext) {
        this.levelDiscountDao = levelDiscountDao;
        this.customerDao = customerDao;
        this.totalAmountStDao = totalAmountStDao;
        this.voucherDao = voucherDao;
        this.totalAmountStGiftDao = totalAmountStGiftDao;
        this.giftService = giftService;
        this.promotionContext = promotionContext;
        init();
    }

    private void init() {
        strategyMap = new HashMap<>();
        strategyMap.put(StrategyType.DEFAULT, new DefaultPromotionStrategy());
        strategyMap.put(StrategyType.CUSTOMER_LEVEL, new CustomerLevelStrategy(levelDiscountDao, customerDao));
        strategyMap.put(StrategyType.TOTAL_AMOUNT, new TotalAmountStrategy(totalAmountStDao, voucherDao, totalAmountStGiftDao, giftService));
    }


    @Override
    public void chooseStrategy(StrategyType type) {
        try {
            promotionContext.setStrategy(strategyMap.get(type));
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyServiceException("00001", "策略更新异常");
        }
    }
}
