package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.CustomerDao;
import com.nju.edu.erp.dao.FinanceDao;
import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.model.po.CustomerPO;
import com.nju.edu.erp.model.po.sheets.CollectSheetPO;
import com.nju.edu.erp.model.po.sheets.PaySheetPO;
import com.nju.edu.erp.model.vo.finance.PaySheetVO;
import com.nju.edu.erp.model.vo.finance.SheetVO;
import com.nju.edu.erp.service.SheetService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaySheetService implements SheetService {

    @Override
    @Transactional
    public void approval(String sheetId, SheetState state, FinanceDao financeDao, CustomerDao customerDao) {
        if (state.equals(SheetState.FAILURE)) {
            financeDao.updateCollectSheetState(sheetId, state);
        } else {
            // 财务类单据仅用通过一级审批
            // 收款单完成审批后，会更改客户的应收数据，应该是减少客户的应收数据
            CollectSheetPO collectSheetPO = financeDao.findCollectSheetById(sheetId);
            BigDecimal totalAmount = collectSheetPO.getTotalAmount();
            // 先获得用户的原始应收数据
            CustomerPO customerPO = customerDao.findOneByName(collectSheetPO.getCustomer());
            BigDecimal receivable = customerPO.getReceivable();
            // 减少客户的应收数据
            receivable = receivable.subtract(totalAmount).setScale(2, 0);
            // 判断更新后的应付是否合法（大于等于0）
            if (receivable.doubleValue() < 0) {
                financeDao.updateCollectSheetState(sheetId, SheetState.FAILURE);
                throw new MyServiceException("F004", "用户应收不应小于0");
            } else {
                financeDao.updateCollectSheetState(sheetId, SheetState.SUCCESS);
                // 将更新写回数据库
                customerDao.updateReceivable(customerPO.getName(), receivable);
            }
        }
    }

    @Override
    public List<SheetVO> getSheetByState(SheetState state, FinanceDao financeDao) {
        List<PaySheetPO> paySheetPOS = financeDao.findPaySheetByState(state);
        List<SheetVO> res = new ArrayList<>();
        for (PaySheetPO each : paySheetPOS) {
            PaySheetVO paySheetVO = new PaySheetVO();
            BeanUtils.copyProperties(each, paySheetVO);
            res.add(paySheetVO);
        }
        return res;
    }
}
