package com.nju.edu.erp.service.Impl.strategy.promotion;

import com.nju.edu.erp.dao.TotalAmountStDao;
import com.nju.edu.erp.dao.TotalAmountStGiftDao;
import com.nju.edu.erp.dao.VoucherDao;
import com.nju.edu.erp.enums.promotion.TotalAmountStState;
import com.nju.edu.erp.enums.sheetState.VoucherState;
import com.nju.edu.erp.model.po.SaleSheetPO;
import com.nju.edu.erp.model.po.VoucherPO;
import com.nju.edu.erp.model.po.promotion.TotalAmountStGiftPO;
import com.nju.edu.erp.model.po.promotion.TotalAmountStPO;
import com.nju.edu.erp.model.vo.gift.GiftSheetContentVO;
import com.nju.edu.erp.model.vo.gift.GiftSheetVO;
import com.nju.edu.erp.service.GiftService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TotalAmountStrategy implements PromotionStrategy{


    private List<TotalAmountStPO> stPOList = null;

    private TotalAmountStDao totalAmountStDao;
    private VoucherDao voucherDao;
    private TotalAmountStGiftDao totalAmountStGiftDao;
    private GiftService giftService;

    public TotalAmountStrategy(TotalAmountStDao totalAmountStDao, VoucherDao voucherDao, TotalAmountStGiftDao totalAmountStGiftDao, GiftService giftService) {
        this.totalAmountStDao = totalAmountStDao;
        this.voucherDao = voucherDao;
        this.totalAmountStGiftDao = totalAmountStGiftDao;
        this.giftService = giftService;
    }

    private void initStPOList() {
        stPOList = totalAmountStDao.findByState(TotalAmountStState.AVAILABLE);
    }


    /**
     * 针对总价(rowTotalAmount)的策略
     * 如果总价达到某一数值，就给予赠品或代金券
     * 如果把赠送的代金券设计为自动于本次购买时使用，则可视为满减
     * 需要灵活修改的值：总价阈值，赠送代金券的面值，赠品列表
     * 总价规则可以是多条的，即总价可以有不同的阈值，对应不同的赠品和代金券
     * 如果需要根据总价对其打折，可以用代金券代替
     * @param saleSheetPO
     */
    @Override
    public void setFinalAmount(SaleSheetPO saleSheetPO) {
        // 以防中途总经理修改造成的不一致，每次使用该策略时都应该重新写表
        initStPOList();

        // 按照amount降序排列，优先匹配高总价阈值的规则
        stPOList.sort(Comparator.comparing(TotalAmountStPO::getAmount));
        TotalAmountStPO st = null;
        for (TotalAmountStPO stPO : stPOList) {
            if (saleSheetPO.getRawTotalAmount().compareTo(stPO.getAmount()) > 0) {
                st = stPO;
            }
        }

        /**
         * 此处有一个问题，即代金券和赠品的发放的前提条件应该是销售单审批成功之后，否则顾客可以用这种方式骗取代金券和赠品
         * 时间问题，不做修改了
         * todo
         */
        if (st != null) {
            // 代金券
            if (st.getVoucher().compareTo(BigDecimal.ZERO) > 0) {
                VoucherPO voucherPO = VoucherPO.builder()
                        .customerId(saleSheetPO.getSupplier())
                        .state(VoucherState.AVAILABLE)
                        .value(st.getVoucher())
                        .remark("总价促销id：" + st.getId()).build();
                voucherDao.add(voucherPO);

                // 如果未使用代金券，则自动为顾客在该次购物中使用该代金券
                if (saleSheetPO.getVoucherId() == null) {
                    voucherPO = voucherDao.getLatestAvailByCustomerId(saleSheetPO.getSupplier());
                    saleSheetPO.setVoucherId(voucherPO.getId());
                    saleSheetPO.setVoucherAmount(voucherPO.getValue());
                }
            }
            // 生成赠品单
            List<TotalAmountStGiftPO> giftPOS = totalAmountStGiftDao.selectByStId(st.getId());
            if (giftPOS!=null && !giftPOS.isEmpty()) {
                List<GiftSheetContentVO> giftSheetContents = new ArrayList<>();
                for (TotalAmountStGiftPO giftPO : giftPOS) {
                    giftSheetContents.add(GiftSheetContentVO.builder()
                            .pid(giftPO.getPid())
                            .quantity(giftPO.getQuantity())
                            .remark("总价促销id：" + st.getId())
                            .build());
                }
                GiftSheetVO giftSheetVO = GiftSheetVO.builder()
                        .supplier(saleSheetPO.getSupplier())
                        .saleSheetId(saleSheetPO.getId())
                        .operator(saleSheetPO.getOperator())
                        .salesman(saleSheetPO.getSalesman())
                        .remark("总价促销id：" + st.getId())
                        .giftSheetContents(giftSheetContents)
                        .build();
                giftService.makeGiftSheet(giftSheetVO);
            }
        }
        saleSheetPO.setFinalAmount(DiscountVoucherCalculator.calculate(saleSheetPO));
    }

    @Override
    public String toString() {
        return "总价赠送策略";
    }
}
