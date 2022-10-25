package com.nju.edu.erp.web.controller;

import com.nju.edu.erp.auth.Authorized;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.model.vo.table.SaleDetailsCondVO;
import com.nju.edu.erp.service.TableService;
import com.nju.edu.erp.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/table")
public class TableController {

    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    /**
     * 查看销售明细表：统计一段时间内商品的销售和销售后退货
     * 筛选条件有：时间区间，商品名，客户，业务员，仓库
     * 返回列表包含的信息：时间（精确到天），商品名，型号，数量，单价，总额
     * 需要支持导出数据
     */
    @GetMapping(value = "/saleDetails")
    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public Response getSaleDetails(@RequestParam(value = "begin") String begin, @RequestParam(value = "end") String end,
            @RequestParam(value = "name", required = false) String name, @RequestParam(value = "supplier", required = false) Integer supplier,
            @RequestParam(value = "salesman", required = false) String salesman) {
        SaleDetailsCondVO saleDetailsCondVO = new SaleDetailsCondVO(begin, end, name, supplier, salesman);
        return Response.buildSuccess(tableService.getSaleDetailsTable(saleDetailsCondVO));
    }

    /**
     * 查看销售明细表：导出Excel
     */
    @GetMapping("/saleDetails/excel")
//    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public void getSaleDetailsExcel(@RequestParam(value = "begin") String begin, @RequestParam(value = "end") String end,
                                   @RequestParam(value = "name", required = false) String name, @RequestParam(value = "supplier", required = false) Integer supplier,
                                   @RequestParam(value = "salesman", required = false) String salesman, HttpServletResponse response) throws IOException {
        SaleDetailsCondVO saleDetailsCondVO = new SaleDetailsCondVO(begin, end, name, supplier, salesman);
        tableService.getSaleDetailsTableExcel(response, saleDetailsCondVO);
    }

    /**
     * 查看经营情况表
     * 统计显示一段时间内的经营收支状况和利润。经营收入显示为折让后，并显示折让了多少
     * 显示的信息：收入类，支出类，利润
     * 对于现有的model层而言，获取数据库历史快照的操作太过复杂，因此简化该方法，统计迄今为止的经营情况
     */
    @GetMapping("/businessHistory")
    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public Response getBusinessHistory() {
        return Response.buildSuccess(tableService.getBusinessHistory());
    }

    @GetMapping("/businessHistory/excel")
//    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public void getBusinessHistoryExcel(HttpServletResponse response) throws IOException {
        tableService.getBusinessHistoryExcel(response);
    }


    // 下面是查看经营历程表中的诸多方法
    // 查看指定条件的收款单
    @GetMapping("/showTargetCollectSheet")
    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public Response showTargetCollectSheet(@RequestParam String beginDateStr,
                                           @RequestParam String endDateStr,
                                           @RequestParam String customer,
                                           @RequestParam String operator) {
        return Response.buildSuccess(this.tableService.getTargetCollectSheet(beginDateStr, endDateStr, customer, operator));
    }
    // 查看指定条件的付款单
    @GetMapping("/showTargetPaySheet")
    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public Response showTargetPaySheet(@RequestParam String beginDateStr,
                                           @RequestParam String endDateStr,
                                           @RequestParam String customer,
                                           @RequestParam String operator) {
        return Response.buildSuccess(this.tableService.getTargetPaySheet(beginDateStr, endDateStr, customer, operator));
    }
    // 查看指定条件的现金费用单
    @GetMapping("/showTargetCashSheet")
    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public Response showTargetCashSheet(@RequestParam String beginDateStr,
                                       @RequestParam String endDateStr,
                                       @RequestParam String operator) {
        return Response.buildSuccess(this.tableService.getTargetCashSheet(beginDateStr, endDateStr, operator));
    }
    // 查看指定条件的工资单
    @GetMapping("/showTargetSalarySheet")
    @Authorized(roles = {Role.ADMIN,Role.FINANCIAL_STAFF,Role.GM})
    public Response showTargetSalarySheet(@RequestParam String beginDateStr,
                                        @RequestParam String endDateStr) {
        return Response.buildSuccess(this.tableService.getTargetSalarySheet(beginDateStr, endDateStr));
    }
}
