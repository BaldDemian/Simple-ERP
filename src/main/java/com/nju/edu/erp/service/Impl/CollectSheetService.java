package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.CustomerDao;
import com.nju.edu.erp.dao.FinanceDao;
import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.model.po.CustomerPO;
import com.nju.edu.erp.model.po.sheets.CollectSheetPO;
import com.nju.edu.erp.model.vo.finance.CollectSheetVO;
import com.nju.edu.erp.model.vo.finance.SheetVO;
import com.nju.edu.erp.service.SheetService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CollectSheetService implements SheetService {

    @Override
    @Transactional
    public void approval(String sheetId, SheetState state, FinanceDao financeDao, CustomerDao customerDao) {
        if (state.equals(SheetState.FAILURE)) {
            financeDao.updateCollectSheetState(sheetId, state);
        } else {
            // 财务类单据仅用通过一级审批
            // 收款单完成审批后，会更改客户的应付数据，应该是减少客户的应付数据
            CollectSheetPO collectSheetPO = financeDao.findCollectSheetById(sheetId);
            BigDecimal totalAmount = collectSheetPO.getTotalAmount();
            // 先获得用户的原始应付数据
            CustomerPO customerPO = customerDao.findOneByName(collectSheetPO.getCustomer());
            BigDecimal payable = customerPO.getPayable();
            // 减少客户的应付数据
            payable = payable.subtract(totalAmount).setScale(2, 0);
            // 判断更新后的应付是否合法（大于等于0）
            if (payable.doubleValue() < 0) {
                financeDao.updateCollectSheetState(sheetId, SheetState.FAILURE);
                throw new MyServiceException("F003", "用户应付不应小于0");
            } else {
                financeDao.updateCollectSheetState(sheetId, SheetState.SUCCESS);
                // 将更新写回数据库
                customerDao.updatePayable(customerPO.getName(), payable);
            }
        }
    }

    @Override
    public List<SheetVO> getSheetByState(SheetState state, FinanceDao financeDao) {
        List<CollectSheetPO> collectSheetPOS = financeDao.findCollectSheetByState(state);
        List<SheetVO> res = new ArrayList<>();
        for (CollectSheetPO each : collectSheetPOS) {
            CollectSheetVO collectSheetVO = new CollectSheetVO();
            BeanUtils.copyProperties(each, collectSheetVO);
            res.add(collectSheetVO);
        }
        return res;
    }
}
