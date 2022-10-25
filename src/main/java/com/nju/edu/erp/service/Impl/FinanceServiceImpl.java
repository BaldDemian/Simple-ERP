package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.CustomerDao;
import com.nju.edu.erp.dao.FinanceDao;
import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.model.po.AccountPO;
import com.nju.edu.erp.model.po.sheets.*;
import com.nju.edu.erp.model.vo.finance.*;
import com.nju.edu.erp.service.FinanceService;
import com.nju.edu.erp.service.SheetService;
import com.nju.edu.erp.utils.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class FinanceServiceImpl implements FinanceService {
    private final FinanceDao financeDao;
    private final CustomerDao customerDao;

    @Autowired
    public FinanceServiceImpl(FinanceDao financeDao, CustomerDao customerDao) {
        this.financeDao = financeDao;
        this.customerDao = customerDao;
    }
    @Override
    public void createAccount(AccountVO accountVO) {
        // 先检查是否已经创建了同名的账户
        AccountPO accountPO = financeDao.findByName(accountVO.getName());
        if (accountPO != null) {
            throw new MyServiceException("F000", "已创建同名账户!");
        }
        accountPO = new AccountPO();
        BeanUtils.copyProperties(accountVO, accountPO);
        financeDao.createAccount(accountPO);
    }

    @Override
    public int deleteAccount(String name) {
        return financeDao.deleteAccount(name);
    }

    @Override
    public List<AccountPO> searchAccounts(String word) {
        return financeDao.findByWord(word);
    }

    @Override
    public void approval(String sheetId, SheetState state, SheetService sheetService) {
        // 通过接口调用 approval 方法
        sheetService.approval(sheetId, state, this.financeDao, this.customerDao);
    }

    @Override
    public List<SheetVO> getSheetByState(String type, SheetState state, SheetService sheetService) {
        return sheetService.getSheetByState(state, this.financeDao);
    }

    @Override
    @Transactional
    public void makeCollectSheet(CollectSheetVO collectSheetVO) {
        CollectSheetPO collectSheetPO = new CollectSheetPO();
        BeanUtils.copyProperties(collectSheetVO, collectSheetPO);
        CollectSheetPO latest = financeDao.getLastCollectSheet();
        String id = IdGenerator.generateSheetId(latest == null ? null : latest.getId(), "SKD");
        collectSheetPO.setId(id);
        collectSheetPO.setState(SheetState.PENDING);
        collectSheetPO.setCreateTime(new Date());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<TransferContentPO> transferContentPOS = new ArrayList<>();
        for (TransferContentVO each : collectSheetVO.getTransferContents()) {
            TransferContentPO transferContentPO = new TransferContentPO();
            BeanUtils.copyProperties(each, transferContentPO);
            transferContentPO.setId(id); // 关联ID
            totalAmount = totalAmount.add(each.getTransferAmount());
            transferContentPOS.add(transferContentPO);
        }
        collectSheetPO.setTotalAmount(totalAmount.setScale(2, 0));
        financeDao.saveCollectSheet(collectSheetPO);
        financeDao.saveTransferContents(transferContentPOS);
    }

    @Override
    @Transactional
    public void makePaySheet(PaySheetVO paySheetVO) {
        PaySheetPO paySheetPO = new PaySheetPO();
        BeanUtils.copyProperties(paySheetVO, paySheetPO);
        PaySheetPO latest = financeDao.getLastPaySheet();
        String id = IdGenerator.generateSheetId(latest == null ? null : latest.getId(), "FKD");
        paySheetPO.setId(id);
        paySheetPO.setState(SheetState.PENDING);
        paySheetPO.setCreateTime(new Date());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<TransferContentPO> transferContentPOS = new ArrayList<>();
        for (TransferContentVO each : paySheetVO.getTransferContents()) {
            TransferContentPO transferContentPO = new TransferContentPO();
            BeanUtils.copyProperties(each, transferContentPO);
            transferContentPO.setId(id); // 关联ID
            totalAmount = totalAmount.add(each.getTransferAmount());
            transferContentPOS.add(transferContentPO);
        }
        paySheetPO.setTotalAmount(totalAmount.setScale(2, 0));
        financeDao.savePaySheet(paySheetPO);
        financeDao.saveTransferContents(transferContentPOS);
    }

    @Override
    @Transactional
    public void makeSalarySheet(SalarySheetVO salarySheetVO) {
        SalarySheetPO salarySheetPO = new SalarySheetPO();
        BeanUtils.copyProperties(salarySheetVO, salarySheetPO);
        SalarySheetPO latest = financeDao.getLastSalarySheet();
        String id = IdGenerator.generateSheetId(latest == null ? null : latest.getId(), "GZD");
        salarySheetPO.setId(id);
        salarySheetPO.setCreateTime(new Date());
        double rawSalary = salarySheetVO.getRawSalary().doubleValue();
        double tax = 0;
        // 通过应发工资计算出税款，使用表驱动
        //----------------------------
        tax += rawSalary * 0.07; // 公积金的个人缴纳部分是工资的7%
        tax += calculateTax(rawSalary); // 仅计算个人所得税
        //----------------------------
        salarySheetPO.setActualSalary(new BigDecimal(rawSalary - tax).setScale(2, 0));
        salarySheetPO.setTax(new BigDecimal(tax).setScale(2, 0));
        // 更新单据状态
        salarySheetPO.setState(SheetState.PENDING);
        // 存入数据库
        financeDao.saveSalarySheet(salarySheetPO);
    }
    public double calculateTax(double salary) {
        // 仅计算个人所得税部分的税款
        double res = 0;
        double[][] standards = new double[][] {
                {0,     5000,  0.00, 0},
                {5000,  8000,  0.03, 90},
                {8000,  17000, 0.10, 900},
                {17000, 30000, 0.20, 2600},
                {30000, 40000, 0.25, 2500},
                {40000, 60000, 0.30, 6000},
                {60000, 85000, 0.35, 8750},
        };
        if (salary > 85000) {
            res = 20840 + (salary - 85000) * 0.45;
        } else {
            for (int i = 0; i < standards.length; i++) {
                if (salary >= standards[i][0] && salary <= standards[i][1]) {
                    // 超过该档位下界但是没有超过上界的
                    res = res + (salary - standards[i][0]) * standards[i][2];
                    break; // 不再上升档位
                } else if (salary > standards[i][1]) {
                    // 加的应该是这个档位的速算值，即数组中的第四个元素
                    res = res + standards[i][3];
                    // 进档
                }
            }
        }
        return res;
    }
    @Override
    @Transactional
    public void makeCashSheet(CashSheetVO cashSheetVO) {
        CashSheetPO cashSheetPO = new CashSheetPO();
        BeanUtils.copyProperties(cashSheetVO, cashSheetPO);
        CashSheetPO latest = financeDao.getLastCashSheet();
        String id = IdGenerator.generateSheetId(latest == null ? null : latest.getId(), "GZD");
        cashSheetPO.setId(id);
        // 现金费用单不用经过审批！！！
        cashSheetPO.setState(SheetState.SUCCESS);
        cashSheetPO.setCreateTime(new Date());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<CashSheetContentPO> cashSheetContentPOS = new ArrayList<>();
        for (CashSheetContentVO each : cashSheetVO.getCashSheetContents()) {
            CashSheetContentPO cashSheetContentPO = new CashSheetContentPO();
            BeanUtils.copyProperties(each, cashSheetContentPO);
            cashSheetContentPO.setId(id); // 关联ID
            totalAmount = totalAmount.add(each.getAmount());
            cashSheetContentPOS.add(cashSheetContentPO);
        }
        cashSheetPO.setTotalAmount(totalAmount.setScale(2, 0));
        financeDao.saveCashSheet(cashSheetPO);
        financeDao.saveCashContents(cashSheetContentPOS);
        // TODO 现金消费单据制定完毕后即更改银行卡数据
//        for (CashSheetContentPO each : cashSheetContentPOS) {
//            // 获取条目的银行卡信息
//        }
    }

    @Override
    public BigDecimal getSalarySum() {
        Double salarySum = financeDao.getSalarySum();
        if (salarySum == null) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.valueOf(salarySum);
        }
    }
}
