package com.nju.edu.erp.service;

import com.nju.edu.erp.dao.*;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.enums.sheetState.PurchaseReturnsSheetState;
import com.nju.edu.erp.enums.sheetState.PurchaseSheetState;
import com.nju.edu.erp.enums.sheetState.WarehouseInputSheetState;
import com.nju.edu.erp.model.po.*;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.model.vo.purchase.PurchaseSheetContentVO;
import com.nju.edu.erp.model.vo.purchase.PurchaseSheetVO;
import com.nju.edu.erp.model.vo.purchaseReturns.PurchaseReturnsSheetContentVO;
import com.nju.edu.erp.model.vo.purchaseReturns.PurchaseReturnsSheetVO;
import com.nju.edu.erp.model.vo.table.BusinessHistoryVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class TableServiceTest {

    @Autowired
    private TableService tableService;

    @Autowired
    private PurchaseSheetDao purchaseSheetDao;

    @Autowired
    PurchaseReturnsSheetDao purchaseReturnsSheetDao;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseReturnsService purchaseReturnsService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private WarehouseInputSheetDao warehouseInputSheetDao;

    /**
     * 现有的进货退货方法中退货商品的单价是根据相应进货商品的单价来填的，而不管进货退货的商品实际传入的单价
     * 因此会导致测试失败
     * 该方法暂且作废
     */
    @Test
    @Transactional
    @Rollback(value = true)
    public void testCountPurchaseReturn() {

        UserVO userVO = UserVO.builder()
                .name("xiaoshoujingli")
                .role(Role.SALE_MANAGER)
                .build();
//        制定进货单
        List<PurchaseSheetContentVO> purchaseSheetContentVOS = new ArrayList<>();
        purchaseSheetContentVOS.add(PurchaseSheetContentVO.builder()
                .pid("0000000000400000")
                .quantity(50)
                .remark("Test1-product1")
                .unitPrice(BigDecimal.valueOf(3200))
                .build());
        purchaseSheetContentVOS.add(PurchaseSheetContentVO.builder()
                .pid("0000000000400001")
                .quantity(60)
                .remark("Test1-product2")
                .unitPrice(BigDecimal.valueOf(4200))
                .build());
        PurchaseSheetVO purchaseSheetVO = PurchaseSheetVO.builder()
                .purchaseSheetContent(purchaseSheetContentVOS)
                .supplier(2)
                .remark("test")
                .build();
        purchaseService.makePurchaseSheet(userVO, purchaseSheetVO);
        PurchaseSheetPO latest = purchaseSheetDao.getLatest();
//        进货单审批
        purchaseService.approval(latest.getId(), PurchaseSheetState.PENDING_LEVEL_2);
        purchaseService.approval(latest.getId(), PurchaseSheetState.SUCCESS);

//        入库单审批
        WarehouseInputSheetPO latest2 = warehouseInputSheetDao.getLatest();
        warehouseService.approvalInputSheet(userVO, latest2.getId(), WarehouseInputSheetState.SUCCESS);

//        制定退货单
        List<PurchaseReturnsSheetContentVO> purchaseReturnsSheetContentVOS = new ArrayList<>();
        purchaseReturnsSheetContentVOS.add(PurchaseReturnsSheetContentVO.builder()
                .pid("0000000000400001")
                .quantity(30)
                .unitPrice(BigDecimal.valueOf(4000))
                .remark("test")
                .build());
        PurchaseReturnsSheetVO purchaseReturnsSheetVO = PurchaseReturnsSheetVO.builder()
                .purchaseReturnsSheetContent(purchaseReturnsSheetContentVOS)
                .purchaseSheetId(latest.getId())
                .createTime(new Date()).build();
        purchaseReturnsService.makePurchaseReturnsSheet(userVO, purchaseReturnsSheetVO);
        PurchaseReturnsSheetPO latest1 = purchaseReturnsSheetDao.getLatest();
//        退货单审批
        purchaseReturnsService.approval(latest1.getId(), PurchaseReturnsSheetState.PENDING_LEVEL_2);
        purchaseReturnsService.approval(latest1.getId(), PurchaseReturnsSheetState.SUCCESS);

        BusinessHistoryVO businessHistory = tableService.getBusinessHistory();
        Assertions.assertEquals(true, businessHistory.getPurchaseReturn().compareTo(BigDecimal.valueOf(-6000))==0);
        System.out.println("purchaseReturn: " + businessHistory.getPurchaseReturn());
    }

    /**
     * 使用lck的本地数据库测试
     */
    @Test
    @Transactional
    @Rollback(value = true)
    public void testCountVoucher() {
        BusinessHistoryVO businessHistory = tableService.getBusinessHistory();
        Assertions.assertEquals(true, businessHistory.getVoucher().compareTo(BigDecimal.valueOf(600))==0);
    }

    /**
     * 使用lck的本地数据库测试
     */
    @Test
    @Transactional
    @Rollback(value = true)
    public void testCostChange() {
        BusinessHistoryVO businessHistory = tableService.getBusinessHistory();
        Assertions.assertEquals(true, businessHistory.getVoucher().compareTo(BigDecimal.valueOf(600))==0);
    }
}
