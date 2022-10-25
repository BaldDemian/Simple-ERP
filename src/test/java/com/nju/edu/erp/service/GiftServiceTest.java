package com.nju.edu.erp.service;

import com.nju.edu.erp.dao.GiftSheetDao;
import com.nju.edu.erp.dao.ProductDao;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.enums.sheetState.GiftSheetState;
import com.nju.edu.erp.model.po.GiftSheetContentPO;
import com.nju.edu.erp.model.po.GiftSheetPO;
import com.nju.edu.erp.model.po.ProductPO;
import com.nju.edu.erp.model.vo.gift.GiftSheetContentVO;
import com.nju.edu.erp.model.vo.gift.GiftSheetVO;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.utils.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
public class GiftServiceTest {
    @Autowired
    private GiftService giftService;

    @Autowired
    private GiftSheetDao giftSheetDao;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDao productDao;

    @Test
    @Transactional
    @Rollback(value = true)
    public void makeGiftSheet() { // 测试赠品单生成是否成功

        List<GiftSheetContentVO> giftSheetContentVOS = Arrays.asList(
                new GiftSheetContentVO(null, null, "0000000000400000", 50, "test"),
                new GiftSheetContentVO(null, null, "0000000000400001", 30, "test")
        );
        GiftSheetVO giftSheetVO = GiftSheetVO.builder()
                .giftSheetContents(giftSheetContentVOS)
                .supplier(2)
                .remark("test")
                .build();

        GiftSheetPO prevSheet = giftSheetDao.getLatestSheet();
        String realSheetId = IdGenerator.generateSheetId(prevSheet == null ? null : prevSheet.getId(), "ZPD");

        giftService.makeGiftSheet(giftSheetVO);
        GiftSheetPO latestSheet = giftSheetDao.getLatestSheet();
        Assertions.assertNotNull(latestSheet);
        Assertions.assertEquals(realSheetId, latestSheet.getId());
        Assertions.assertEquals(GiftSheetState.PENDING, latestSheet.getState());

        String sheetId = latestSheet.getId();
        Assertions.assertNotNull(sheetId);
        List<GiftSheetContentPO> content = giftSheetDao.findContentBySheetId(sheetId);
        content.sort(Comparator.comparing(GiftSheetContentPO::getPid));
        Assertions.assertEquals(2, content.size());
        Assertions.assertEquals("0000000000400000", content.get(0).getPid());
        Assertions.assertEquals("0000000000400001", content.get(1).getPid());
    }

    /**
     * 使用lck的本地数据库
     */
    @Test
    @Transactional
    @Rollback(value = true)
    public void approval() {
        List<GiftSheetContentVO> giftSheetContentVOS = Arrays.asList(
                new GiftSheetContentVO(null, null, "0000000000400000", 50, "test"),
                new GiftSheetContentVO(null, null, "0000000000400001", 30, "test")
        );
        GiftSheetVO giftSheetVO = GiftSheetVO.builder()
                .giftSheetContents(giftSheetContentVOS)
                .supplier(2)
                .remark("test")
                .build();
        giftService.makeGiftSheet(giftSheetVO);
        GiftSheetPO latestSheet = giftSheetDao.getLatestSheet();

        Integer quantity_0  = productDao.findById("0000000000400000").getQuantity();
        Integer quantity_1 = productDao.findById("0000000000400001").getQuantity();

        giftService.approval(latestSheet.getId(), GiftSheetState.SUCCESS);

        Integer quantity_0_  = productDao.findById("0000000000400000").getQuantity();
        Integer quantity_1_ = productDao.findById("0000000000400001").getQuantity();

        // test pending to success
        latestSheet = giftSheetDao.getLatestSheet();
        Assertions.assertEquals(true, latestSheet.getState().compareTo(GiftSheetState.SUCCESS)==0);
        Assertions.assertEquals(true, quantity_0 - quantity_0_ == 50);
        Assertions.assertEquals(true, quantity_1 - quantity_1_ == 30);

        // test success to failure
        try {
            giftService.approval(latestSheet.getId(), GiftSheetState.FAILURE);
            Assertions.assertTrue(false);
        } catch (RuntimeException e) {
            latestSheet = giftSheetDao.getLatestSheet();
            Integer quantity_0__  = productDao.findById("0000000000400000").getQuantity();
            Integer quantity_1__ = productDao.findById("0000000000400001").getQuantity();
            Assertions.assertEquals(true, latestSheet.getState().compareTo(GiftSheetState.SUCCESS)==0);
            Assertions.assertEquals(quantity_0_, quantity_0__);
            Assertions.assertEquals(quantity_1_, quantity_1__);
        }
    }
    
}
