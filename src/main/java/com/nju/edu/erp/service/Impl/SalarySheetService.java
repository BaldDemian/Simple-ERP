package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.CustomerDao;
import com.nju.edu.erp.dao.FinanceDao;
import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.model.po.sheets.SalarySheetPO;
import com.nju.edu.erp.model.vo.finance.SalarySheetVO;
import com.nju.edu.erp.model.vo.finance.SheetVO;
import com.nju.edu.erp.service.SheetService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class SalarySheetService implements SheetService {

    @Override
    @Transactional
    public void approval(String sheetId, SheetState state, FinanceDao financeDao, CustomerDao customerDao) {
        if (state.equals(SheetState.FAILURE)) {
            financeDao.updateCollectSheetState(sheetId, state);
        } else {
            // 工资单审批成功后要干啥？
            financeDao.updateCollectSheetState(sheetId, state);
        }
    }

    @Override
    public List<SheetVO> getSheetByState(SheetState state, FinanceDao financeDao) {
        List<SalarySheetPO> salarySheetPOS = financeDao.findSalarySheetByState(state);
        List<SheetVO> res = new ArrayList<>();
        for (SalarySheetPO each : salarySheetPOS) {
            SalarySheetVO salarySheetVO = new SalarySheetVO();
            BeanUtils.copyProperties(each, salarySheetVO);
            res.add(salarySheetVO);
        }
        return res;
    }
}
