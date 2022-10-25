package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.GiftSheetDao;
import com.nju.edu.erp.dao.ProductDao;
import com.nju.edu.erp.dao.WarehouseDao;
import com.nju.edu.erp.enums.sheetState.GiftSheetState;
import com.nju.edu.erp.enums.sheetState.PurchaseReturnsSheetState;
import com.nju.edu.erp.model.po.*;
import com.nju.edu.erp.model.vo.ProductInfoVO;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.model.vo.gift.GiftSheetContentVO;
import com.nju.edu.erp.model.vo.gift.GiftSheetVO;
import com.nju.edu.erp.model.vo.warehouse.WarehouseOutputFormContentVO;
import com.nju.edu.erp.model.vo.warehouse.WarehouseOutputFormVO;
import com.nju.edu.erp.service.GiftService;
import com.nju.edu.erp.service.ProductService;
import com.nju.edu.erp.utils.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GiftServiceImpl implements GiftService {

    @Autowired
    private GiftSheetDao giftSheetDao;

    @Autowired
    private WarehouseDao warehouseDao;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDao productDao;


    @Override
    @Transactional
    public void makeGiftSheet(GiftSheetVO giftSheetVO) {
        GiftSheetPO giftSheetPO = new GiftSheetPO();
        BeanUtils.copyProperties(giftSheetVO, giftSheetPO);

        giftSheetPO.setCreateTime(new Date());
        giftSheetPO.setState(GiftSheetState.PENDING);

        GiftSheetPO latest = giftSheetDao.getLatestSheet();
        String id = IdGenerator.generateSheetId(latest==null? null : latest.getId(), "ZPD");
        giftSheetPO.setId(id);

        List<GiftSheetContentPO> contentPOS = new ArrayList<>();
        for (GiftSheetContentVO contentVO : giftSheetVO.getGiftSheetContents()) {
            GiftSheetContentPO contentPO = new GiftSheetContentPO();
            BeanUtils.copyProperties(contentVO, contentPO);
            contentPO.setGiftSheetId(id);
            contentPOS.add(contentPO);
        }
        giftSheetDao.saveBatchSheetContent(contentPOS);
        giftSheetDao.saveSheet(giftSheetPO);
    }

    @Override
    public List<GiftSheetVO> getGiftSheetByState(GiftSheetState state) {
        return null;
    }

    @Override
    @Transactional
    public void approval(String giftSheetId, GiftSheetState state) {
        if (state.equals(GiftSheetState.FAILURE)) {
            GiftSheetPO giftSheet = giftSheetDao.findSheetById(giftSheetId);
            if (giftSheet.getState() == GiftSheetState.SUCCESS) throw new RuntimeException("状态更新失败");
            int effectLines = giftSheetDao.updateSheetState(giftSheetId, state);
            if (effectLines == 0) throw new RuntimeException("状态更新失败");
        } else {
            GiftSheetState prevState;
            if (state.equals(GiftSheetState.SUCCESS)) {
                prevState = GiftSheetState.PENDING;
            } else {
                throw new RuntimeException("状态更新失败");
            }
            int effectLines = giftSheetDao.updateSheetStateOnPrev(giftSheetId, prevState, state);
            if (effectLines == 0) throw new RuntimeException("状态更新失败");

            // 修改状态
            List<GiftSheetContentPO> contents = giftSheetDao.findContentBySheetId(giftSheetId);
            for (GiftSheetContentPO content : contents) {
                String pid = content.getPid();
                Integer quantity = content.getQuantity();
                // 更新最新商品数量
                ProductPO product = productDao.findById(pid);
                if (product.getQuantity().compareTo(content.getQuantity()) < 0) {
                    throw new RuntimeException("商品不足");
                }
                product.setQuantity(product.getQuantity() - content.getQuantity());
                productDao.updateById(product);
                // 更新库存信息
                // 查询获取同一商品的不同批次信息，按进价排序
                int remainAmount = content.getQuantity();
                List<WarehousePO> availableWarehouses = warehouseDao.findByPidOrderByPurchasePricePos(pid);
                for (WarehousePO availableWarehouse : availableWarehouses) {
                    if (availableWarehouse.getQuantity() >= remainAmount) {
                        availableWarehouse.setQuantity(remainAmount);
                        warehouseDao.deductQuantity(availableWarehouse);
                        break;
                    } else {
                        remainAmount = remainAmount - availableWarehouse.getQuantity();
                        warehouseDao.deductQuantity(availableWarehouse);
                    }
                }
            }

        }
    }

    @Override
    public GiftSheetVO getGiftSheetById(String giftSheetId) {
        return null;
    }
}
