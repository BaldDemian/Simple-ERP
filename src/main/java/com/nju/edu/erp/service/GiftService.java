package com.nju.edu.erp.service;

import com.nju.edu.erp.enums.sheetState.GiftSheetState;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.model.vo.gift.GiftSheetVO;

import java.util.List;

public interface GiftService {


    /**
     * 制定赠品单
     * @param userVO
     * @param giftSheetVO
     */
    void makeGiftSheet(GiftSheetVO giftSheetVO);

    /**
     * 根据单据状态获取销售单
     * @param state
     * @return
     */
    List<GiftSheetVO> getGiftSheetByState(GiftSheetState state);

    /**
     * 审批单据
     * @param giftSheetId
     * @param state
     */
    void approval(String giftSheetId, GiftSheetState state);

    /**
     * 根据赠品单Id搜索赠品单信息
     * @param giftSheetId 销售单Id
     * @return 赠品单全部信息
     */
    GiftSheetVO getGiftSheetById(String giftSheetId);
}
