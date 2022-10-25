package com.nju.edu.erp.web.controller;

import com.nju.edu.erp.auth.Authorized;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.model.vo.finance.*;
import com.nju.edu.erp.service.FinanceService;
import com.nju.edu.erp.service.Impl.CollectSheetService;
import com.nju.edu.erp.service.Impl.PaySheetService;
import com.nju.edu.erp.service.Impl.SalarySheetService;
import com.nju.edu.erp.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/finance")
public class FinanceController {
    private final FinanceService financeService;

    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;

    }

    @PostMapping("/createAccount")
    @Authorized(roles = {Role.ADMIN, Role.FINANCIAL_STAFF})
    public Response createAccount(@RequestBody AccountVO accountVO) {
        try {
            financeService.createAccount(accountVO);
        } catch (MyServiceException myServiceException) {
            return new Response(myServiceException.getCode(), myServiceException.toString());
        }
        return Response.buildSuccess();
    }

    @GetMapping("/deleteAccount")
    @Authorized(roles = {Role.ADMIN, Role.FINANCIAL_STAFF})
    public Response deleteAccount(@RequestParam String name) {
        int res = financeService.deleteAccount(name);
        if (res == 0) {
            return Response.buildFailed("F001", "不存在指定账户!");
        } else {
            return Response.buildSuccess();
        }
    }

    @GetMapping("/queryAccount")
    @Authorized(roles = {Role.ADMIN, Role.FINANCIAL_STAFF})
    public Response queryAccount(@RequestParam String word) {
        return Response.buildSuccess(financeService.searchAccounts(word));
    }

    @GetMapping("/approval")
    @Authorized(roles = {Role.ADMIN, Role.GM})
    public Response approval(@RequestParam("sheetId") String sheetId, @RequestParam("state") SheetState state) {
        // 现金费用单不用审批！！
        // 财务类单据只用经过一级审批即可
        // state应该是SUCCESS | FAILURE 其一
        if (state.equals(SheetState.FAILURE) || state.equals(SheetState.SUCCESS)) {
            if (sheetId.startsWith("SKD")) {
                // 如何避免SheetService实例的频繁创建和销毁？
                try {
                    financeService.approval(sheetId, state, new CollectSheetService());
                } catch (MyServiceException myServiceException) {
                    return Response.buildFailed("F003", "客户应付不应小于0！");
                }

            } else if (sheetId.startsWith("FKD")) {
                try {
                    financeService.approval(sheetId, state, new PaySheetService());
                } catch (MyServiceException myServiceException) {
                    return Response.buildFailed("F004", "客户应收不应小于0！");
                }
            }  else if (sheetId.startsWith("GZD")) {
                financeService.approval(sheetId, state, new SalarySheetService());
            }
            return Response.buildSuccess();
        } else {
            return Response.buildFailed("F002", "更新状态失败");
        }
    }

    // 根据状态获取各类单据，主要供总经理审批用
    @GetMapping("/showSheet")
    @Authorized(roles = {Role.ADMIN, Role.GM, Role.FINANCIAL_STAFF})
    public Response showSheetByState(@RequestParam String type, @RequestParam SheetState state) {
        switch (type) {
            case "SKD":
                return Response.buildSuccess(financeService.getSheetByState(type, state, new CollectSheetService()));
            case "FKD":
                return Response.buildSuccess(financeService.getSheetByState(type, state, new PaySheetService()));
            case "GZD":
                return Response.buildSuccess(financeService.getSheetByState(type, state, new SalarySheetService()));
            default:
                return Response.buildFailed("F005", "获取单据失败");
        }
    }

    // 感觉制定单据难以抽象，所以还是分开写
    // 制定付款单
    @PostMapping("/makeCollectSheet")
    @Authorized(roles = {Role.ADMIN, Role.FINANCIAL_STAFF})
    public Response makeCollectSheet(@RequestBody CollectSheetVO collectSheetVO) {
        financeService.makeCollectSheet(collectSheetVO);
        return Response.buildSuccess();
    }
    // 制定收款单
    @PostMapping("/makePaySheet")
    @Authorized(roles = {Role.ADMIN, Role.FINANCIAL_STAFF})
    public Response makePaySheet(@RequestBody PaySheetVO paySheetVO) {
        financeService.makePaySheet(paySheetVO);
        return Response.buildSuccess();
    }
    // 制定现金费用单
    @PostMapping("/makeCashSheet")
    @Authorized(roles = {Role.ADMIN, Role.FINANCIAL_STAFF})
    public Response makeCashSheet(@RequestBody CashSheetVO cashSheetVO) {
        financeService.makeCashSheet(cashSheetVO);
        return Response.buildSuccess();
    }
    @PostMapping("/makeSalarySheet")
    @Authorized(roles = {Role.ADMIN, Role.FINANCIAL_STAFF})
    public Response makeSalarySheet(@RequestBody SalarySheetVO salarySheetVO) {
        financeService.makeSalarySheet(salarySheetVO);
        return Response.buildSuccess();
    }
}
