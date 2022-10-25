package com.nju.edu.erp.dao;

import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.model.po.AccountPO;
import com.nju.edu.erp.model.po.sheets.*;
import com.nju.edu.erp.model.vo.finance.CashSheetVO;
import com.nju.edu.erp.model.vo.finance.CollectSheetVO;
import com.nju.edu.erp.model.vo.finance.PaySheetVO;
import com.nju.edu.erp.model.vo.finance.SalarySheetVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface FinanceDao {
    void createAccount(AccountPO accountPO);

    int deleteAccount(String name);

    int changeAmount(AccountPO accountPO);

    AccountPO findByName(String name);

    List<AccountPO> findByWord(String word);

    // 获取最近一条收款单
    CollectSheetPO getLastCollectSheet();
    // 获取最近一条付款单
    PaySheetPO getLastPaySheet();
    // 获取最近一条现金费用单
    CashSheetPO getLastCashSheet();
    // 获取最近一条工资单
    SalarySheetPO getLastSalarySheet();
    // 存入一条收款单
    void saveCollectSheet(CollectSheetPO sheetPO);
    // 存入一条付款单
    void savePaySheet(PaySheetPO sheetPO);
    // 存入一条现金费用单
    void saveCashSheet(CashSheetPO sheetPO);
    // 存入一条工资单
    void saveSalarySheet(SalarySheetPO sheetPO);
    // 存入收款单和付款单的转账列表
    void saveTransferContents(List<TransferContentPO> transferContentPOS);
    // 存入现金消费单的条目
    void saveCashContents(List<CashSheetContentPO> cashSheetContentPOS);
    // 更新收款单的状态
    void updateCollectSheetState(String sheetId, SheetState state);
    // 更新付款单的状态
    void updatePaySheetState(String sheetId, SheetState state);
    // 更新工资单的状态
    void updateSalarySheetState(String sheetId, SheetState state);
    // 根据状态获取收款单
    List<CollectSheetPO> findCollectSheetByState(SheetState state);
    // 根据状态获取付款单
    List<PaySheetPO> findPaySheetByState(SheetState state);
    // 根据状态获取工资单
    List<SalarySheetPO> findSalarySheetByState(SheetState state);
    // 按ID获取收款单
    CollectSheetPO findCollectSheetById(String sheetId);
    // 按ID获取付款单
    PaySheetPO findPaySheetById(String sheetId);
    // 按ID获取工资单
    SalarySheetPO findSalarySheetById(String sheetId);
    // 按ID获取现金费用单
    CashSheetPO findCashSheetById(String sheetId);
    // 获取指定时间段内的、指定客户、指定操作员的收款单
    List<CollectSheetVO> getTargetCollectSheet(Date beginDate, Date endDate, String customer, String operator);
    // 获取指定时间段内的、指定客户、指定操作员的付款单
    List<PaySheetVO> getTargetPaySheet(Date beginDate, Date endDate, String customer, String operator);
    // 获取指定时间段内的、指定操作员的现金费用单
    List<CashSheetVO> getTargetCashSheet(Date beginDate, Date endDate, String operator);
    // 获取指定时间段内的工资单
    List<SalarySheetVO> getTargetSalarySheet(Date beginDate, Date endDate);
    // 获取迄今为止所有员工的工资总和（不算审批失败和未审批的）
    Double getSalarySum();
}
