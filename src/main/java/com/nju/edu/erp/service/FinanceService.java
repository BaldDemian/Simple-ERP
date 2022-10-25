package com.nju.edu.erp.service;

import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.model.po.AccountPO;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.model.vo.finance.*;

import java.math.BigDecimal;
import java.util.List;

public interface FinanceService {
    /**
     * 新增银行账户
     * @param accountVO 前端传过来的数据
     */
    void createAccount(AccountVO accountVO);

    /**
     * 删除银行账户
     * @param name 银行账户名称
     */
    int deleteAccount(String name);

    /**
     * 修改账户属性？？？
     */

    /**
     * 查询账户
     * @param word 关键字，要能支持模糊关键字查询
     */
    List<AccountPO> searchAccounts(String word);

    void approval(String sheetId, SheetState state, SheetService sheetService);

    List<SheetVO> getSheetByState(String type, SheetState state, SheetService sheetService);

    void makeCollectSheet(CollectSheetVO collectSheetVO);
    void makePaySheet(PaySheetVO paySheetVO);
    void makeSalarySheet(SalarySheetVO salarySheetVO);
    void makeCashSheet(CashSheetVO cashSheetVO);

    BigDecimal getSalarySum();
}
