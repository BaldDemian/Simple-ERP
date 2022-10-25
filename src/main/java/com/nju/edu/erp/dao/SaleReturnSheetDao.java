package com.nju.edu.erp.dao;

import com.nju.edu.erp.enums.sheetState.SaleReturnSheetState;
import com.nju.edu.erp.model.po.SaleReturnSheetContentPO;
import com.nju.edu.erp.model.po.SaleReturnSheetPO;
import com.nju.edu.erp.model.po.WarehousePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface SaleReturnSheetDao {
    /**
     * 获取最近一条销售退货单
     * @return 最近一条销售退货单
     */
    SaleReturnSheetPO getLatest();

    /**
     * 存入一条销售退货单记录
     * @param toSave 一条销售退货单记录
     * @return 影响的行数
     */
    int save(SaleReturnSheetPO toSave);

    /**
     * 把销售退货单上的具体内容存入数据库
     * @param SaleReturnsSheetContent 销售退货单上的具体内容
     */
    void saveBatch(List<SaleReturnSheetContentPO> SaleReturnSheetContent);

    /**
     * 返回所有销售退货单
     * @return 销售退货单列表
     */
    List<SaleReturnSheetPO> findAll();

    /**
     * 根据state返回销售退货单
     * @param state 销售退货单状态
     * @return 销售退货单列表
     */
    List<SaleReturnSheetPO> findAllByState(SaleReturnSheetState state);

    /**
     * 根据 saleReturnSheetId 找到条目， 并更新其状态为state
     * @param saleReturnSheetId 销售退货单id
     * @param state 销售退货单状态
     * @return 影响的条目数
     */
    int updateState(String saleReturnSheetId, SaleReturnSheetState state);

    /**
     * 根据 saleReturnSheetId 和 prevState 找到条目，并更新其状态为state
     * @param purchaseReturnsSheetId 销售退货单id
     * @param prevState 销售退货单之前的状态
     * @param state 销售退货单状态
     * @return 影响的条目数
     */
    int updateStateV2(String saleReturnSheetId, SaleReturnSheetState prevState, SaleReturnSheetState state);

    /**
     * 通过saleReturnSheetId找到对应条目
     * @param saleReturnSheetId 销售退货单id
     * @return
     */
    SaleReturnSheetPO findOneById(String saleReturnSheetId);

    /**
     * 通过saleReturnSheetId找到对应的content条目
     * @param saleReturnSheetId
     * @return
     */
    List<SaleReturnSheetContentPO> findContentBySaleReturnSheetId(String saleReturnSheetId);

    /**
     * 根据销售退货单ID，商品ID返回在对应的销售单对应的出库单中的不同批次的同一种商品的WarehousePO实体
     * @param saleReturnSheetId
     * @return 批次号
     */
    List<WarehousePO> find(String saleSheetId, String pId);

    List<SaleReturnSheetPO> findSheetByTime(Date beginDate, Date endDate);

    List<SaleReturnSheetPO> findSheetBySaleSheetId(String id);
}
