package com.nju.edu.erp.service;

import com.nju.edu.erp.dao.CustomerDao;
import com.nju.edu.erp.dao.FinanceDao;
import com.nju.edu.erp.enums.sheetState.SheetState;
import com.nju.edu.erp.model.po.CustomerPO;
import com.nju.edu.erp.model.vo.finance.SheetVO;

import java.util.List;

// 抽象出单据的公共操作（制定订单操作难以抽象）
public interface SheetService {
    /**
     * 审批单据
     */
    void approval(String sheetId, SheetState state, FinanceDao financeDao, CustomerDao customerDao);

    /**
     * 按照状态查找单据
     */
    List<SheetVO> getSheetByState(SheetState state, FinanceDao financeDao);

}
