package com.nju.edu.erp.web.controller;

import com.nju.edu.erp.auth.Authorized;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.enums.promotion.StrategyType;
import com.nju.edu.erp.enums.promotion.TotalAmountStState;
import com.nju.edu.erp.model.vo.promotion.TotalAmountStVO;
import com.nju.edu.erp.service.Impl.strategy.promotion.PromotionContext;
import com.nju.edu.erp.service.LevelDiscountStService;
import com.nju.edu.erp.service.PromotionStrategyService;
import com.nju.edu.erp.service.TotalAmountStService;
import com.nju.edu.erp.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/promotion")
public class PromotionController {
    private final LevelDiscountStService levelDiscountStService;
    private final TotalAmountStService totalAmountStService;
    private PromotionStrategyService promotionStrategyService;
    private PromotionContext promotionContext;

    @Autowired
    public PromotionController(LevelDiscountStService levelDiscountStService, TotalAmountStService totalAmountStService, PromotionContext promotionContext, PromotionStrategyService promotionStrategyService, PromotionContext promotionContext1) {
        this.levelDiscountStService = levelDiscountStService;
        this.totalAmountStService = totalAmountStService;
        this.promotionStrategyService = promotionStrategyService;
        this.promotionContext = promotionContext1;
    }

    @PutMapping("/strategy/choose")
    @Authorized(roles = {Role.GM, Role.ADMIN})
    public Response chooseStrategy(@RequestBody Integer type) {
        promotionStrategyService.chooseStrategy(StrategyType.values()[type]);
        return Response.buildSuccess();
    }

    @GetMapping("/levelDiscountSt/all")
    public Response getAllLevelDiscount() {
        return Response.buildSuccess(levelDiscountStService.findAll());
    }

    @GetMapping("/levelDiscountSt/{level}")
    public Response getLevelDiscountByLevel(@PathVariable Integer level) {
        return Response.buildSuccess(levelDiscountStService.findByLevel(level));
    }

    @PutMapping("/levelDiscountSt/update")
    @Authorized(roles = {Role.GM, Role.ADMIN})
    public Response updateByLevel(@RequestParam("level") Integer level, @RequestParam("discount")BigDecimal discount) {
        levelDiscountStService.updateDiscountByLevel(level, discount);
        return Response.buildSuccess();
    }

    @GetMapping("/totalAmountSt/all")
    public Response getAllTotalAmount() {
        return Response.buildSuccess(totalAmountStService.getAll());
    }

    @PutMapping("/totalAmountSt/add")
    @Authorized(roles = {Role.GM, Role.ADMIN})
    public Response addTotalAmountSt(@RequestBody TotalAmountStVO totalAmountStVO) {
        totalAmountStService.add(totalAmountStVO);
        return Response.buildSuccess();
    }

    @PutMapping("/totalAmountSt/state/update")
    @Authorized(roles = {Role.GM, Role.ADMIN})
    public Response updateTotalAmountStateById(@RequestParam Integer id, @RequestParam TotalAmountStState state) {
        totalAmountStService.updateStateById(id, state);
        return Response.buildSuccess();
    }

    @GetMapping("/now")
    public Response getNowStrategy() {
        return Response.buildSuccess(promotionContext.getStrategyName());
    }

}
