package com.nju.edu.erp.dao;

import com.nju.edu.erp.enums.sheetState.GiftSheetState;
import com.nju.edu.erp.model.po.GiftSheetContentPO;
import com.nju.edu.erp.model.po.GiftSheetPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface GiftSheetDao {
    GiftSheetPO getLatestSheet();

    void saveBatchSheetContent(List<GiftSheetContentPO> contentPOS);

    void saveSheet(GiftSheetPO giftSheetPO);

    List<GiftSheetContentPO> findContentBySheetId(String sheetId);

    GiftSheetPO findSheetById(String giftSheetId);

    int updateSheetState(String giftSheetId, GiftSheetState state);

    int updateSheetStateOnPrev(String giftSheetId, GiftSheetState prevState, GiftSheetState state);

    List<GiftSheetPO> findSheetByState(GiftSheetState state);
}
