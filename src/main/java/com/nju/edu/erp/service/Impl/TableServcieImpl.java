package com.nju.edu.erp.service.Impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.nju.edu.erp.dao.*;
import com.nju.edu.erp.enums.sheetState.GiftSheetState;
import com.nju.edu.erp.enums.sheetState.PurchaseReturnsSheetState;
import com.nju.edu.erp.enums.sheetState.SaleReturnSheetState;
import com.nju.edu.erp.enums.sheetState.SaleSheetState;
import com.nju.edu.erp.model.po.*;
import com.nju.edu.erp.model.vo.finance.CashSheetVO;
import com.nju.edu.erp.model.vo.finance.CollectSheetVO;
import com.nju.edu.erp.model.vo.finance.PaySheetVO;
import com.nju.edu.erp.model.vo.finance.SalarySheetVO;
import com.nju.edu.erp.model.vo.table.BusinessHistoryVO;
import com.nju.edu.erp.model.vo.table.SaleDetailsCondVO;
import com.nju.edu.erp.model.vo.table.SaleDetailsVO;
import com.nju.edu.erp.service.FinanceService;
import com.nju.edu.erp.service.TableService;
import com.nju.edu.erp.utils.DateStrParser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableServcieImpl implements TableService {

    private final SaleSheetDao saleSheetDao;
    private final SaleReturnSheetDao saleReturnSheetDao;
    private final ProductDao productDao;
    private final PurchaseReturnsSheetDao purchaseReturnsSheetDao;
    private final PurchaseSheetDao purchaseSheetDao;
    private final WarehouseDao warehouseDao;
    private final FinanceDao financeDao;
    private final GiftSheetDao giftSheetDao;
    private final FinanceService financeService;

    @Autowired
    public TableServcieImpl(SaleSheetDao saleSheetDao, SaleReturnSheetDao saleReturnSheetDao, ProductDao productDao, PurchaseReturnsSheetDao purchaseReturnsSheetDao, PurchaseSheetDao purchaseSheetDao, WarehouseDao warehouseDao, FinanceDao financeDao, GiftSheetDao giftSheetDao, FinanceService financeService) {
        this.saleSheetDao = saleSheetDao;
        this.saleReturnSheetDao = saleReturnSheetDao;
        this.productDao = productDao;
        this.purchaseReturnsSheetDao = purchaseReturnsSheetDao;
        this.purchaseSheetDao = purchaseSheetDao;
        this.warehouseDao = warehouseDao;
        this.financeDao = financeDao;
        this.giftSheetDao = giftSheetDao;
        this.financeService = financeService;
    }


    /**
     * @param saleDetailsCondVO
     * beginDateStr: ?????????????????????????????????????????????yyyy-MM-dd?????????"2022-05-12"
     * endDateStr: ?????????????????????????????????????????????yyyy-MM-dd?????????"2022-05-12"
     * name: ?????????
     * supplier: ??????
     * salesman: ?????????
     * @return saleDetailsVO
     * ????????????????????????
     * ?????????
     * ??????
     * ??????
     * ??????
     * ??????
     */
    @Override
    public Map<String, List<SaleDetailsVO>> getSaleDetailsTable(SaleDetailsCondVO saleDetailsCondVO) {

        // if begin after end
        Date beginDate = DateStrParser.strToDate(saleDetailsCondVO.getBeginDateStr());
        Date endDate = DateStrParser.strToDate(saleDetailsCondVO.getEndDateStr());
        if (beginDate.after(endDate)) {
            return null;
        }

        // get sheets by time
        // sale???????????????????????????????????????????????????????????????
        List<SaleSheetPO> saleSheetPOS = saleSheetDao.findSheetByTime(beginDate, endDate).stream()
                .filter(s -> s.getState()==SaleSheetState.SUCCESS)
                .collect(Collectors.toList());
        // return???????????????????????????????????????????????????????????????
        List<SaleReturnSheetPO> saleReturnSheetPOS = saleReturnSheetDao.findSheetByTime(beginDate, endDate).stream()
                .filter(s -> s.getState()==SaleReturnSheetState.SUCCESS)
                .collect(Collectors.toList());
        // by supplier and salesman
        // sale
        saleSheetPOS = saleSheetPOS.stream()
                .filter(s -> (saleDetailsCondVO.getSupplier()==null?true:saleDetailsCondVO.getSupplier().compareTo(s.getSupplier())==0)
                        &&(saleDetailsCondVO.getSalesman()==null?true:saleDetailsCondVO.getSalesman().compareTo(s.getSalesman())==0)).collect(Collectors.toList());
        // return
        saleReturnSheetPOS = saleReturnSheetPOS.stream()
                .filter(s -> {
                    SaleSheetPO saleSheetPO = saleSheetDao.findSheetById(s.getSaleSheetId());
                    Integer supplier = saleSheetPO.getSupplier();
                    String salesman = saleSheetPO.getSalesman();
                    return (saleDetailsCondVO.getSupplier()==null?true:saleDetailsCondVO.getSupplier().compareTo(supplier)==0)
                        &&(saleDetailsCondVO.getSalesman()==null?true:saleDetailsCondVO.getSalesman().compareTo(salesman)==0);
                }).collect(Collectors.toList());

        // get contents
        // sale
        List<SaleSheetContentPO> contents = new ArrayList<>();
        for (SaleSheetPO saleSheetPO : saleSheetPOS) {
            List<SaleSheetContentPO> content = saleSheetDao.findContentBySheetId(saleSheetPO.getId());
            contents.addAll(content);
        }
        //return
        List<SaleReturnSheetContentPO> r_contents = new ArrayList<>();
        for (SaleReturnSheetPO saleReturnSheetPO : saleReturnSheetPOS) {
            List<SaleReturnSheetContentPO> r_content = saleReturnSheetDao.findContentBySaleReturnSheetId(saleReturnSheetPO.getId());
            r_contents.addAll(r_content);
        }
        // by name
        if (saleDetailsCondVO.getName() != null) {
            // sale
            contents = contents.stream()
                    .filter(c -> {
                        ProductPO productPO = productDao.findById(c.getPid());
                        String name = productPO.getName();
                        return saleDetailsCondVO.getName().compareTo(name) == 0;
                    }).collect(Collectors.toList());
            // return
            r_contents = r_contents.stream()
                    .filter(c -> {
                        ProductPO productPO = productDao.findById(c.getPid());
                        String name = productPO.getName();
                        return saleDetailsCondVO.getName().compareTo(name) == 0;
                    }).collect(Collectors.toList());
        }

        // bean copy
        // sale
        List<SaleDetailsVO> saleDetails = contents.stream().map(c -> {
            SaleDetailsVO saleDetailsVO = new SaleDetailsVO();

            BeanUtils.copyProperties(c, saleDetailsVO);
//            saleDetailsVO.setUnitPrice(c.getUnitPrice());
//            saleDetailsVO.setTotalPrice(c.getTotalPrice());
//            saleDetailsVO.setQuantity(c.getQuantity());
            ProductPO productPO = productDao.findById(c.getPid());
            saleDetailsVO.setName(productPO.getName());
            saleDetailsVO.setType(productPO.getType());
            SaleSheetPO saleSheetPO = saleSheetDao.findSheetById(c.getSaleSheetId());
            saleDetailsVO.setDate(DateStrParser.dateToStr(saleSheetPO.getCreateTime()));
            return saleDetailsVO;
        }).collect(Collectors.toList());
        // return
        List<SaleDetailsVO> returnDetails = r_contents.stream().map(c -> {
            SaleDetailsVO saleDetailsVO = new SaleDetailsVO();

            BeanUtils.copyProperties(c, saleDetailsVO);
//            saleDetailsVO.setUnitPrice(c.getUnitPrice());
//            saleDetailsVO.setTotalPrice(c.getTotalPrice());
//            saleDetailsVO.setQuantity(c.getQuantity());
            ProductPO productPO = productDao.findById(c.getPid());
            saleDetailsVO.setName(productPO.getName());
            saleDetailsVO.setType(productPO.getType());
            SaleReturnSheetPO saleReturnSheetPO = saleReturnSheetDao.findOneById(c.getSaleReturnSheetId());
            saleDetailsVO.setDate(DateStrParser.dateToStr(saleReturnSheetPO.getCreateTime()));
            return saleDetailsVO;
        }).collect(Collectors.toList());

        Map<String, List<SaleDetailsVO>> data = new HashMap<>();
        data.put("sale", saleDetails);
        data.put("return", returnDetails);
        return data;
    }

    @Override
    public void getSaleDetailsTableExcel(HttpServletResponse response, SaleDetailsCondVO saleDetailsCondVO) throws IOException {
        // ????????????
        Map<String, List<SaleDetailsVO>> saleDetailsTable = getSaleDetailsTable(saleDetailsCondVO);
        List<SaleDetailsVO> saleDetailsVOS = new ArrayList<>();
        saleDetailsVOS.addAll(saleDetailsTable.get("sale"));
        saleDetailsVOS.addAll(saleDetailsTable.get("return"));

        ExcelWriter writer = ExcelUtil.getWriter(true);
        // ???????????????
        writer.addHeaderAlias("date", "??????");
        writer.addHeaderAlias("name", "?????????");
        writer.addHeaderAlias("type", "??????");
        writer.addHeaderAlias("quantity", "??????");
        writer.addHeaderAlias("unitPrice", "??????");
        writer.addHeaderAlias("totalPrice", "??????");
        writer.write(saleDetailsVOS, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset:utf-8");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String fileName= year + "-" + month + "-" + day;
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");
        ServletOutputStream outputStream= response.getOutputStream();
        writer.flush(outputStream,true);
        outputStream.close();
        writer.close();
    }

    @Override
    public BusinessHistoryVO getBusinessHistory() {

        BigDecimal sale = BigDecimal.ZERO;
        BigDecimal voucher = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;

        List<ProductPO> productPOS = productDao.findAll();

        Map<String, BigDecimal> recentPurchasePrices = productDao.findAll().stream()
                .collect(HashMap::new, (m, p)->m.put(p.getId(), p.getRecentPp()), HashMap::putAll);

        // sale
        List<SaleSheetPO> saleSheetPOS = saleSheetDao.findAllSheet().stream()
                .filter(s -> s.getState()==SaleSheetState.SUCCESS).collect(Collectors.toList());
        for (SaleSheetPO saleSheetPO : saleSheetPOS) {
            sale = sale.add(saleSheetPO.getRawTotalAmount());
            voucher = voucher.add(saleSheetPO.getVoucherAmount());
            discount = discount.add(saleSheetPO.getRawTotalAmount().multiply(BigDecimal.ONE.subtract(saleSheetPO.getDiscount())));
            List<SaleSheetContentPO> contents = saleSheetDao.findContentBySheetId(saleSheetPO.getId());
            for (SaleSheetContentPO content : contents) {
                String pid = content.getPid();
                Integer quantity = content.getQuantity();
                BigDecimal recentPp = recentPurchasePrices.get(pid);
                cost = cost.add(recentPp.multiply(BigDecimal.valueOf(quantity)));
            }
        }

        // return
        List<SaleReturnSheetPO> saleReturnSheetPOS = saleReturnSheetDao.findAll().stream()
                .filter(s -> s.getState()==SaleReturnSheetState.SUCCESS).collect(Collectors.toList());
        for (SaleReturnSheetPO saleReturnSheetPO : saleReturnSheetPOS) {
            // sale???discount????????????????????????model?????????????????????????????????todo

            List<SaleReturnSheetContentPO> r_contents = saleReturnSheetDao.findContentBySaleReturnSheetId(saleReturnSheetPO.getId());
            for (SaleReturnSheetContentPO r_content : r_contents) {
                String pid = r_content.getPid();
                Integer quantity = r_content.getQuantity();
                BigDecimal recentPp = recentPurchasePrices.get(pid);
                cost = cost.subtract(recentPp.multiply(BigDecimal.valueOf(quantity)));
            }
        }

        BusinessHistoryVO businessHistoryVO = new BusinessHistoryVO();

        // ????????????????????????)
        businessHistoryVO.setSale(sale);

        // ?????????
        businessHistoryVO.setDiscount(discount);

        // ???????????????>0????????????
        businessHistoryVO.setCostChange(calculateCostChange());

        // ??????????????????>0????????????
        businessHistoryVO.setPurchaseReturn(calculatePurchaseReturn());

        // ???????????????
        businessHistoryVO.setVoucher(voucher);

        // ??????????????????
        businessHistoryVO.setSaleCost(cost);

        // ??????????????????
        businessHistoryVO.setLaborCost(financeService.getSalarySum());

        // ??????????????????
        businessHistoryVO.setGiftCost(calculateGiftCost());


        // ?????????=?????????????????????+????????????+???????????????
        BigDecimal revenue = businessHistoryVO.getSale();
        if (businessHistoryVO.getCostChange().compareTo(BigDecimal.ZERO) > 0) {
            revenue = revenue.add(businessHistoryVO.getCostChange());
        }
        if (businessHistoryVO.getPurchaseReturn().compareTo(BigDecimal.ZERO) > 0) {
            revenue = revenue.add(businessHistoryVO.getPurchaseReturn());
        }
        businessHistoryVO.setRevenue(revenue);

        // ??????????????????=?????????-??????
        businessHistoryVO.setRevenueAfterDiscount(revenue.subtract(discount));

        // ?????????=?????????+????????????+????????????+????????????+???????????????
        BigDecimal spending = BigDecimal.ZERO;
        spending = spending.add(businessHistoryVO.getVoucher());
        spending = spending.add(businessHistoryVO.getSaleCost());
        spending = spending.add(businessHistoryVO.getLaborCost());
        if (businessHistoryVO.getCostChange().compareTo(BigDecimal.ZERO) < 0) {
            spending = spending.subtract(businessHistoryVO.getCostChange());
        }
        if (businessHistoryVO.getPurchaseReturn().compareTo(BigDecimal.ZERO) < 0) {
            spending = spending.subtract(businessHistoryVO.getPurchaseReturn());
        }
        businessHistoryVO.setSpending(spending);

        // ??????=????????????-??????
        BigDecimal revenueAfterDiscount = businessHistoryVO.getRevenueAfterDiscount();
        BigDecimal spending_ = businessHistoryVO.getSpending();
        businessHistoryVO.setProfit(revenueAfterDiscount.subtract(spending_));

        return businessHistoryVO;
    }

    @Override
    public void getBusinessHistoryExcel(HttpServletResponse response) throws IOException {
        // ????????????
        BusinessHistoryVO businessHistory = getBusinessHistory();

        ExcelWriter writer = ExcelUtil.getWriter(true);
        // ???????????????
        writer.addHeaderAlias("sale", "????????????");
        writer.addHeaderAlias("costChange", "????????????");
        writer.addHeaderAlias("purchaseReturn", "???????????????");
        writer.addHeaderAlias("voucher", "???????????????");
        writer.addHeaderAlias("saleCost", "????????????");
        writer.addHeaderAlias("laborCost", "????????????");
        writer.addHeaderAlias("giftCost", "????????????");
        writer.addHeaderAlias("revenue", "?????????");
        writer.addHeaderAlias("discount", "?????????");
        writer.addHeaderAlias("spending", "?????????");
        writer.addHeaderAlias("profit", "??????");

        List<BusinessHistoryVO> businessHistoryVOS = Arrays.asList(businessHistory);
        writer.write(businessHistoryVOS, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset:utf-8");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String fileName= year + "-" + month + "-" + day;
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");
        ServletOutputStream outputStream= response.getOutputStream();
        writer.flush(outputStream,true);
        outputStream.close();
        writer.close();
    }


    private BigDecimal calculateGiftCost() {
        // ????????????????????????????????????
        Map<String, BigDecimal> recentPurchasePrices = productDao.findAll().stream()
                .collect(HashMap::new, (m, p)->m.put(p.getId(), p.getRecentPp()), HashMap::putAll);

        BigDecimal giftCost = BigDecimal.ZERO;
        // ?????????????????????
        List<GiftSheetPO> giftSheetPOS = giftSheetDao.findSheetByState(GiftSheetState.SUCCESS);
        for (GiftSheetPO giftSheetPO : giftSheetPOS) {
            List<GiftSheetContentPO> contents = giftSheetDao.findContentBySheetId(giftSheetPO.getId());
            for (GiftSheetContentPO content : contents) {
                String pid = content.getPid();
                Integer quantity = content.getQuantity();
                BigDecimal unitPrice = recentPurchasePrices.get(pid);
                giftCost = giftCost.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            }
        }

        return giftCost;
    }


    @Override
    public List<CollectSheetVO> getTargetCollectSheet(String beginDateStr, String endDateStr, String customer, String operator) {
        Date beginDate;
        Date endDate;
        if (beginDateStr.equals("")) {
            beginDate = null;
        } else {
            beginDate = parseDateStr(beginDateStr);
        }
        if (endDateStr.equals("")) {
            endDate = null;
        } else {
            endDate = parseDateStr(endDateStr);
        }
        if (customer.equals("")) {
            customer = null;
        }
        if (operator.equals("")) {
            operator = null;
        }
        return financeDao.getTargetCollectSheet(beginDate, endDate, customer, operator);
    }

    @Override
    public List<PaySheetVO> getTargetPaySheet(String beginDateStr, String endDateStr, String customer, String operator) {
        Date beginDate;
        Date endDate;
        if (beginDateStr.equals("")) {
            beginDate = null;
        } else {
            beginDate = parseDateStr(beginDateStr);
        }
        if (endDateStr.equals("")) {
            endDate = null;
        } else {
            endDate = parseDateStr(endDateStr);
        }
        if (customer.equals("")) {
            customer = null;
        }
        if (operator.equals("")) {
            operator = null;
        }
        return financeDao.getTargetPaySheet(beginDate, endDate, customer, operator);
    }

    @Override
    public List<CashSheetVO> getTargetCashSheet(String beginDateStr, String endDateStr, String operator) {
        Date beginDate;
        Date endDate;
        if (beginDateStr.equals("")) {
            beginDate = null;
        } else {
            beginDate = parseDateStr(beginDateStr);
        }
        if (endDateStr.equals("")) {
            endDate = null;
        } else {
            endDate = parseDateStr(endDateStr);
        }
        if (operator.equals("")) {
            operator = null;
        }
        return financeDao.getTargetCashSheet(beginDate, endDate, operator);
    }

    @Override
    public List<SalarySheetVO> getTargetSalarySheet(String beginDateStr, String endDateStr) {
        Date beginDate;
        Date endDate;
        if (beginDateStr.equals("")) {
            beginDate = null;
        } else {
            beginDate = parseDateStr(beginDateStr);
        }
        if (endDateStr.equals("")) {
            endDate = null;
        } else {
            endDate = parseDateStr(endDateStr);
        }
        return financeDao.getTargetSalarySheet(beginDate, endDate);
    }

    /**
     * ?????????????????????????????????????????????????????????Date??????
     * @param dateStr ?????????"yyyy-MM-dd HH:mm:ss"?????????
     */
    private Date parseDateStr(String dateStr) {
        int year = Integer.parseInt(dateStr.substring(0, 4));
        int month = Integer.parseInt(dateStr.substring(5, 7));
        int day = Integer.parseInt(dateStr.substring(8, 10));
        int hour = Integer.parseInt(dateStr.substring(11, 13));
        int minute = Integer.parseInt(dateStr.substring(14, 16));
        int second = Integer.parseInt(dateStr.substring(17));
        // ???Calendar?????????Date?????????Calendar??????month??????0???????????????
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????rollback????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ?????????????????????model???????????????????????????????????????????????????????????????????????????
     * @return
     */
    private BigDecimal calculateCostChange() {
        Map<String, BigDecimal> recentPurchasePrices = productDao.findAll().stream()
                .collect(HashMap::new, (m, p)->m.put(p.getId(), p.getRecentPp()), HashMap::putAll);
        List<WarehousePO> warehousePOS = warehouseDao.findAll();
        BigDecimal costChange = BigDecimal.ZERO;
        for (WarehousePO warehousePO : warehousePOS) {
            Integer quantity = warehousePO.getQuantity();
            BigDecimal pP = warehousePO.getPurchasePrice();
            BigDecimal rPp = recentPurchasePrices.get(warehousePO.getPid());
            BigDecimal diff = rPp.subtract(pP);
            costChange = costChange.add(diff.multiply(BigDecimal.valueOf(quantity)));
        }
        return costChange;
    }

    public BigDecimal calculatePurchaseReturn() {
        BigDecimal purchaseReturn = BigDecimal.ZERO;
        List<PurchaseReturnsSheetPO> purchaseReturnsSheetPOS = purchaseReturnsSheetDao.findAll();
        purchaseReturnsSheetPOS = purchaseReturnsSheetPOS.stream()
                .filter(s -> s.getState()==PurchaseReturnsSheetState.SUCCESS).collect(Collectors.toList());
        for (PurchaseReturnsSheetPO s : purchaseReturnsSheetPOS) {
            List<PurchaseSheetContentPO> contents = purchaseSheetDao.findContentByPurchaseSheetId(s.getPurchaseSheetId());
            List<PurchaseReturnsSheetContentPO> r_contents = purchaseReturnsSheetDao.findContentByPurchaseReturnsSheetId(s.getId());
            for (PurchaseReturnsSheetContentPO r_c : r_contents) {
                String pid = r_c.getPid();
                BigDecimal r_unitPrice = r_c.getUnitPrice();
                Integer quantity = r_c.getQuantity();
//                todo
//                ?????????????????????????????????????????????????????????????????????????????????...
                Optional<PurchaseSheetContentPO> c = contents.stream().filter(psc -> psc.getPid().compareTo(pid) == 0).findFirst();
                BigDecimal unitPrice = c.get().getUnitPrice();
                BigDecimal diffPrice = r_unitPrice.subtract(unitPrice);
                BigDecimal diff = diffPrice.multiply(BigDecimal.valueOf(quantity));
                purchaseReturn = purchaseReturn.add(diff);
            }
        }
        return purchaseReturn;
    }
}
