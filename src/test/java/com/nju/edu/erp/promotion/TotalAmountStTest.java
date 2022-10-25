package com.nju.edu.erp.promotion;

import com.nju.edu.erp.dao.GiftSheetDao;
import com.nju.edu.erp.dao.SaleSheetDao;
import com.nju.edu.erp.dao.VoucherDao;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.enums.promotion.StrategyType;
import com.nju.edu.erp.enums.sheetState.GiftSheetState;
import com.nju.edu.erp.model.po.GiftSheetPO;
import com.nju.edu.erp.model.po.SaleSheetPO;
import com.nju.edu.erp.model.po.VoucherPO;
import com.nju.edu.erp.model.vo.Sale.SaleSheetContentVO;
import com.nju.edu.erp.model.vo.Sale.SaleSheetVO;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.model.vo.promotion.TotalAmountStVO;
import com.nju.edu.erp.service.PromotionStrategyService;
import com.nju.edu.erp.service.SaleService;
import com.nju.edu.erp.service.TotalAmountStService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@SpringBootTest
public class TotalAmountStTest {

    private SaleSheetDao saleSheetDao;

    private SaleService saleService;

    private PromotionStrategyService strategyService;

    private TotalAmountStService totalAmountStService;

    private GiftSheetDao giftSheetDao;

    private VoucherDao voucherDao;

    @Autowired
    public TotalAmountStTest(SaleSheetDao saleSheetDao, SaleService saleService, PromotionStrategyService strategyService, TotalAmountStService totalAmountStService, GiftSheetDao giftSheetDao, VoucherDao voucherDao) {
        this.saleSheetDao = saleSheetDao;
        this.saleService = saleService;
        this.strategyService = strategyService;
        this.totalAmountStService = totalAmountStService;
        this.giftSheetDao = giftSheetDao;
        this.voucherDao = voucherDao;
    }

    @Test
    @Transactional
    @Rollback(value = true)
    public void testStrategy() {
        // 设置销售策略
        strategyService.chooseStrategy(StrategyType.TOTAL_AMOUNT);

        // 新建一条匹配规则：满100000送5000代金券且增送”0000000000400000“*2和"0000000000400000"*3
        Map<String, Integer> gifts = new HashMap<>();
        gifts.put("0000000000400000", 2);
        gifts.put("0000000000400001", 3);
        TotalAmountStVO totalAmountStVO = new TotalAmountStVO(null, BigDecimal.valueOf(100000), BigDecimal.valueOf(5000), gifts);
        totalAmountStService.add(totalAmountStVO);

        // 新建一条匹配规则：满50000送2000代金券且增送”0000000000400000“*1和"0000000000400000"*2
        gifts = new HashMap<>();
        gifts.put("0000000000400000", 1);
        gifts.put("0000000000400001", 2);
        totalAmountStVO = new TotalAmountStVO(null, BigDecimal.valueOf(50000), BigDecimal.valueOf(2000), gifts);
        totalAmountStService.add(totalAmountStVO);

        // 新建一条匹配规则：满20000送1000代金券且增送”0000000000400000“*1和"0000000000400000"*1
        gifts = new HashMap<>();
        gifts.put("0000000000400000", 1);
        gifts.put("0000000000400001", 1);
        totalAmountStVO = new TotalAmountStVO(null, BigDecimal.valueOf(20000), BigDecimal.valueOf(1000), gifts);
        totalAmountStService.add(totalAmountStVO);


        // 制定销售单
        UserVO userVO = UserVO.builder()
                .name("xiaoshoujingli")
                .role(Role.SALE_MANAGER)
                .build();
        List<SaleSheetContentVO> saleSheetContentVOS = Arrays.asList(
                new SaleSheetContentVO("0000000000400000", 10, BigDecimal.valueOf(3200), null, "test"),
                new SaleSheetContentVO("0000000000400001", 5, BigDecimal.valueOf(4200), null, "test")
        );
        SaleSheetVO saleSheetVO = SaleSheetVO.builder()
                .saleSheetContent(saleSheetContentVOS)
                .supplier(2)
                .discount(BigDecimal.valueOf(1))
                .voucherAmount(BigDecimal.valueOf(0))
                .remark("Test1")
                .build();
        saleService.makeSaleSheet(userVO, saleSheetVO);

        // 检查销售单
        SaleSheetPO latestSheet = saleSheetDao.getLatestSheet();
        Assertions.assertNotNull(latestSheet);
        // 该策略没有折扣
        Assertions.assertEquals(0, latestSheet.getDiscount().compareTo(BigDecimal.ONE));
        // 赠送并自动使用2000元代金券
        Assertions.assertEquals(0, latestSheet.getVoucherAmount().compareTo(BigDecimal.valueOf(2000)));
        Assertions.assertEquals(0, latestSheet.getRawTotalAmount().compareTo(BigDecimal.valueOf(53000.00)));
        // 53000*1-2000=51000
        Assertions.assertEquals(0, latestSheet.getFinalAmount().compareTo(BigDecimal.valueOf(51000.00)));

        // 检查赠品单
        GiftSheetPO latestGiftSheet = giftSheetDao.getLatestSheet();
        Assertions.assertEquals(0, latestGiftSheet.getState().compareTo(GiftSheetState.PENDING));
        Assertions.assertEquals(0, latestGiftSheet.getSaleSheetId().compareTo(latestSheet.getId()));
        Assertions.assertEquals(0, latestGiftSheet.getSupplier().compareTo(latestSheet.getSupplier()));

        // 检查代金券
        VoucherPO latestVoucher = voucherDao.getLatestAvailByCustomerId(latestSheet.getSupplier());
        Assertions.assertEquals(0, latestVoucher.getValue().compareTo(BigDecimal.valueOf(2000)));

    }
}
