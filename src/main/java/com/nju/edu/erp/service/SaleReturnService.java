package com.nju.edu.erp.service;

import com.nju.edu.erp.enums.sheetState.SaleReturnSheetState;
import com.nju.edu.erp.model.vo.SaleReturns.SaleReturnSheetVO;
import com.nju.edu.erp.model.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.List;

// 制定销售退货单 + 销售经理审批/总经理二级审批 + 更新客户表 + 更新库存
public interface SaleReturnService {
    /**
     * 制定销售退货单
     * @param userVO
     * @param
     */
    void makeSaleReturnSheet(UserVO userVO, SaleReturnSheetVO saleReturnSheetVO);

    /**
     * 根据状态获取销售退货单(state == null 则获取所有进货退货单)
     * @param state 销售退货单状态
     * @return 销售退货单
     */
    List<SaleReturnSheetVO> getSaleReturnSheetByState(SaleReturnSheetState state);

    /**
     * 根据销售退货单id进行审批(state == "待二级审批"/"审批完成"/"审批失败")
     * 在controller层进行权限控制
     * @param purchaseReturnsSheetId 进货退货单id
     * @param state 进货退货单修改后的状态
     */
    void approval(String saleReturnSheetId, SaleReturnSheetState state);
}
