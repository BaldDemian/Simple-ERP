package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.*;
import com.nju.edu.erp.enums.sheetState.SaleReturnSheetState;
import com.nju.edu.erp.model.po.*;
import com.nju.edu.erp.model.vo.SaleReturns.SaleReturnSheetContentVO;
import com.nju.edu.erp.model.vo.SaleReturns.SaleReturnSheetVO;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.service.CustomerService;
import com.nju.edu.erp.service.ProductService;
import com.nju.edu.erp.service.SaleReturnService;
import com.nju.edu.erp.service.WarehouseService;
import com.nju.edu.erp.utils.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SaleReturnServiceImpl implements SaleReturnService {
    SaleReturnSheetDao saleReturnSheetDao;

    ProductService productService;

    ProductDao productDao;

    SaleSheetDao saleSheetDao;

    CustomerService customerService;

    WarehouseService warehouseService;

    WarehouseDao warehouseDao;

    @Autowired
    public SaleReturnServiceImpl(SaleReturnSheetDao saleReturnSheetDao, ProductService productService, CustomerService customerService, WarehouseService warehouseService, ProductDao productDao, SaleSheetDao saleSheetDao, WarehouseDao warehouseDao) {
        this.saleReturnSheetDao = saleReturnSheetDao;
        this.productService = productService;
        this.customerService = customerService;
        this.warehouseService = warehouseService;
        this.productDao = productDao;
        this.saleSheetDao = saleSheetDao;
        this.warehouseDao =  warehouseDao;
    }

    /**
     * 制定销售退货单
     * @param userVO
     * @param saleReturnSheetVO
     */
    @Override
    @Transactional
    public void makeSaleReturnSheet(UserVO userVO, SaleReturnSheetVO saleReturnSheetVO) {
        SaleReturnSheetPO saleReturnSheetPO = new SaleReturnSheetPO();
        BeanUtils.copyProperties(saleReturnSheetVO, saleReturnSheetPO);
        // 此处根据制定单据人员确定操作员
        saleReturnSheetPO.setOperator(userVO.getName());
        saleReturnSheetPO.setCreateTime(new Date());
        SaleReturnSheetPO latest = saleReturnSheetDao.getLatest();
        String id = IdGenerator.generateSheetId(latest == null ? null : latest.getId(), "XSTHD");
        saleReturnSheetPO.setId(id);
        saleReturnSheetPO.setState(SaleReturnSheetState.PENDING_LEVEL_1);
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 根据销售退货单关联的销售单查询到销售单的详细信息
        List<SaleSheetContentPO> saleSheetContent = saleSheetDao.findContentBySheetId(saleReturnSheetPO.getSaleSheetId());
        Map<String, SaleSheetContentPO> map = new HashMap<>();
        for(SaleSheetContentPO item : saleSheetContent) {
            map.put(item.getPid(), item);
            // 将商品ID与其对应的SaleSheetContent关联起来
            // 注意覆盖问题，但是考虑到同一笔销售单中的unitPrice是相同的，所以应该问题不大
        }
        List<SaleReturnSheetContentPO> sContentPOList = new ArrayList<>();
        // 先获取关联的销售单
        SaleSheetPO saleSheetPO = saleSheetDao.findSheetById(saleReturnSheetPO.getSaleSheetId());
        for(SaleReturnSheetContentVO content : saleReturnSheetVO.getSaleReturnSheetContent()) {
            SaleReturnSheetContentPO sContentPO = new SaleReturnSheetContentPO();
            BeanUtils.copyProperties(content,sContentPO); // 将VO中的属性拷贝进PO
            sContentPO.setSaleReturnSheetId(id);
            // 获得销售退货单的每一行在销售单中对应的那一行（通过pId对应）
            SaleSheetContentPO item = map.get(sContentPO.getPid());
            sContentPO.setUnitPrice(item.getUnitPrice()); // 用销售单的定价初始化销售退货单的定价
            // 以下是最终退货单价的详细计算原则
            // 1.每个商品实际要退的价格为 单价 * 折扣 - 消费券作用在该单个商品上的优惠价格
            // 2.“消费券作用在该单个商品上的优惠价格”——即按单品在总价中的占比来计算分走了多少优惠额度
            // 3.优惠券不退回
            BigDecimal temp = sContentPO.getUnitPrice();
            // 获取折扣
            BigDecimal discount = saleSheetPO.getDiscount();
            // 获取消费券价格
            BigDecimal voucherAmount = saleSheetPO.getVoucherAmount();
            // 计算该商品的单个在消费券上占到的比率
            BigDecimal total = saleSheetPO.getRawTotalAmount();
            BigDecimal rate = temp.divide(total, 5, 0);
            // 计算该商品的单个在消费券上占到的价格
            BigDecimal portion = voucherAmount.multiply(rate);
            // temp = temp * discount - portion
            temp = temp.multiply(discount).subtract(portion);
            temp = temp.setScale(2, 0); // 保留两位小数，否则数据库会报错
            sContentPO.setUnitPrice(temp);
            BigDecimal unitPrice = sContentPO.getUnitPrice();
            // 计算出退货总额= 数量 * 单价，注意优惠券不退
            sContentPO.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(sContentPO.getQuantity())).setScale(2, 0));
            sContentPOList.add(sContentPO);
            totalAmount = totalAmount.add(sContentPO.getTotalPrice().setScale(2, 0));
        }
        saleReturnSheetDao.saveBatch(sContentPOList);
        saleReturnSheetPO.setTotalAmount(totalAmount);
        saleReturnSheetDao.save(saleReturnSheetPO);
    }

    /**
     * 根据状态获取销售退货单
     * @param state 销售退货单状态
     * @return
     */
    @Override
    public List<SaleReturnSheetVO> getSaleReturnSheetByState(SaleReturnSheetState state) {
        List<SaleReturnSheetVO> res = new ArrayList<>();
        List<SaleReturnSheetPO> all;
        if(state == null) {
            all = saleReturnSheetDao.findAll();
        } else {
            all = saleReturnSheetDao.findAllByState(state);
        }
        for(SaleReturnSheetPO po: all) {
            SaleReturnSheetVO vo = new SaleReturnSheetVO();
            BeanUtils.copyProperties(po, vo); // 将po的属性拷贝至vo
            List<SaleReturnSheetContentPO> alll = saleReturnSheetDao.findContentBySaleReturnSheetId(po.getId());
            List<SaleReturnSheetContentVO> vos = new ArrayList<>();
            for (SaleReturnSheetContentPO p : alll) {
                SaleReturnSheetContentVO v = new SaleReturnSheetContentVO();
                BeanUtils.copyProperties(p, v);
                vos.add(v);
            }
            vo.setSaleReturnSheetContent(vos);
            res.add(vo);
        }
        return res;
    }

    /**
     * 根据销售退货单ID审批销售退货单
     * @param saleReturnSheetId
     * @param state 进货退货单修改后的状态\
     * TODO 处理欲退货数量大于实际销售数量的情况
     */
    @Override
    @Transactional
    public void approval(String saleReturnSheetId, SaleReturnSheetState state) {
        // 找到指定ID的销售退货单
        SaleReturnSheetPO saleReturnSheet = saleReturnSheetDao.findOneById(saleReturnSheetId);
        if(state.equals(SaleReturnSheetState.FAILURE)) {
            if(saleReturnSheet.getState() == SaleReturnSheetState.SUCCESS) throw new RuntimeException("状态更新失败");
            int effectLines = saleReturnSheetDao.updateState(saleReturnSheetId, state);
            if(effectLines == 0) throw new RuntimeException("状态更新失败");
        }
        else {
            SaleReturnSheetState prevState;
            if(state.equals(SaleReturnSheetState.SUCCESS)) {
                prevState = SaleReturnSheetState.PENDING_LEVEL_2;
            } else if(state.equals(SaleReturnSheetState.PENDING_LEVEL_2)) {
                prevState = SaleReturnSheetState.PENDING_LEVEL_1;
            } else {
                throw new RuntimeException("状态更新失败");
            }
            int effectLines = saleReturnSheetDao.updateStateV2(saleReturnSheetId, prevState, state);
            if(effectLines == 0) throw new RuntimeException("状态更新失败");
            if(state.equals(SaleReturnSheetState.SUCCESS)) {
                // 审批完成，修改一系列状态，尤其是对Warehouse模块的操作，销售退货是加库存
                // 找到本销售退货单对应的Content
                List<SaleReturnSheetContentPO> contents = saleReturnSheetDao.findContentBySaleReturnSheetId(saleReturnSheetId);
                // 关于找批次的问题：销售单关联了一张库存出库单，出库单的内容上才有商品的批次
                for (SaleReturnSheetContentPO contentPO : contents) {
                    // 获取商品ID
                    String pID = contentPO.getPid();
                    // 获取商品要退货的数量
                    int quantity = contentPO.getQuantity();
                    // 在销售退货单->销售单->出库单->出库单内容 中找到同一商品的不同批次的实体型
                    // 要说明的是WarehousePO实体型中仅有batchId,quantity,pid是有用的
                    List<WarehousePO> warehousePOS = saleReturnSheetDao.find(saleReturnSheet.getSaleSheetId(), pID);
                    List<WarehousePO> toUpdate = new ArrayList<>(); // 实际要更新的WarehousePO
                    // 按照warehousePOS中的顺序进行退货
                    for (WarehousePO warehousePO : warehousePOS) {
                        if (warehousePO.getQuantity() >= quantity) {
                            // 更新库存并break
                            // 方法是在Warehouse中用批次和商品ID找到实际的那个WarehousePO
                            WarehousePO actualPO = warehouseDao.findOneByPidAndBatchId(warehousePO.getPid(), warehousePO.getBatchId());
                            if(actualPO == null) throw new RuntimeException("单据发生错误！请联系管理员！");
                            actualPO.setQuantity(actualPO.getQuantity() + quantity);
                            toUpdate.add(actualPO);
                            quantity = 0;
                            break;
                        } else {
                            // 更新库存更新quantity
                            WarehousePO actualPO = warehouseDao.findOneByPidAndBatchId(warehousePO.getPid(), warehousePO.getBatchId());
                            actualPO.setQuantity(actualPO.getQuantity() + warehousePO.getQuantity());
                            toUpdate.add(actualPO);
                            quantity = quantity - warehousePO.getQuantity();
                        }
                    }
                    // 如果此时欲退货数量还是大于0，说明欲退货数量超过实际销售的产品数量
                    if (quantity > 0) {
                        saleReturnSheetDao.updateState(saleReturnSheetId, SaleReturnSheetState.FAILURE);
                        throw new RuntimeException("欲退货数量大于实际销售数量，审批失败！");
                    } else {
                        // 更新数据库
                        for (WarehousePO actualPO : toUpdate) {
                            warehouseDao.setQuantity(actualPO);
                        }
                    }
                }
            }
        }
    }
}
