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
     * beginDateStr: 开始时间（精确到天），格式：“yyyy-MM-dd”，如"2022-05-12"
     * endDateStr: 结束时间（精确到天），格式：“yyyy-MM-dd”，如"2022-05-12"
     * name: 商品名
     * supplier: 客户
     * salesman: 业务员
     * @return saleDetailsVO
     * 时间（精确到天）
     * 商品名
     * 型号
     * 数量
     * 单价
     * 总额
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
        // sale，注意，只有审批成功的单据才是真实有效的的
        List<SaleSheetPO> saleSheetPOS = saleSheetDao.findSheetByTime(beginDate, endDate).stream()
                .filter(s -> s.getState()==SaleSheetState.SUCCESS)
                .collect(Collectors.toList());
        // return，注意，只有审批成功的单据才是真实有效的的
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
        // 准备数据
        Map<String, List<SaleDetailsVO>> saleDetailsTable = getSaleDetailsTable(saleDetailsCondVO);
        List<SaleDetailsVO> saleDetailsVOS = new ArrayList<>();
        saleDetailsVOS.addAll(saleDetailsTable.get("sale"));
        saleDetailsVOS.addAll(saleDetailsTable.get("return"));

        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 定义标题名
        writer.addHeaderAlias("date", "时间");
        writer.addHeaderAlias("name", "商品名");
        writer.addHeaderAlias("type", "型号");
        writer.addHeaderAlias("quantity", "数量");
        writer.addHeaderAlias("unitPrice", "单价");
        writer.addHeaderAlias("totalPrice", "总额");
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
            // sale，discount需要处理，但现有model使得这个处理十分复杂，todo

            List<SaleReturnSheetContentPO> r_contents = saleReturnSheetDao.findContentBySaleReturnSheetId(saleReturnSheetPO.getId());
            for (SaleReturnSheetContentPO r_content : r_contents) {
                String pid = r_content.getPid();
                Integer quantity = r_content.getQuantity();
                BigDecimal recentPp = recentPurchasePrices.get(pid);
                cost = cost.subtract(recentPp.multiply(BigDecimal.valueOf(quantity)));
            }
        }

        BusinessHistoryVO businessHistoryVO = new BusinessHistoryVO();

        // 销售收入（折扣前)
        businessHistoryVO.setSale(sale);

        // 折让额
        businessHistoryVO.setDiscount(discount);

        // 成本调价，>0则为收入
        businessHistoryVO.setCostChange(calculateCostChange());

        // 进退货差价，>0则为收入
        businessHistoryVO.setPurchaseReturn(calculatePurchaseReturn());

        // 代金券支出
        businessHistoryVO.setVoucher(voucher);

        // 销售成本支出
        businessHistoryVO.setSaleCost(cost);

        // 人力成本支出
        businessHistoryVO.setLaborCost(financeService.getSalarySum());

        // 赠品成本支出
        businessHistoryVO.setGiftCost(calculateGiftCost());


        // 总收入=销售（折扣前）+成本调价+进退货差价
        BigDecimal revenue = businessHistoryVO.getSale();
        if (businessHistoryVO.getCostChange().compareTo(BigDecimal.ZERO) > 0) {
            revenue = revenue.add(businessHistoryVO.getCostChange());
        }
        if (businessHistoryVO.getPurchaseReturn().compareTo(BigDecimal.ZERO) > 0) {
            revenue = revenue.add(businessHistoryVO.getPurchaseReturn());
        }
        businessHistoryVO.setRevenue(revenue);

        // 折让后总收入=总收入-折扣
        businessHistoryVO.setRevenueAfterDiscount(revenue.subtract(discount));

        // 总支出=代金券+销售成本+人力成本+成本调价+进退货差价
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

        // 利润=折后收入-成本
        BigDecimal revenueAfterDiscount = businessHistoryVO.getRevenueAfterDiscount();
        BigDecimal spending_ = businessHistoryVO.getSpending();
        businessHistoryVO.setProfit(revenueAfterDiscount.subtract(spending_));

        return businessHistoryVO;
    }

    @Override
    public void getBusinessHistoryExcel(HttpServletResponse response) throws IOException {
        // 准备数据
        BusinessHistoryVO businessHistory = getBusinessHistory();

        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 定义标题名
        writer.addHeaderAlias("sale", "销售收入");
        writer.addHeaderAlias("costChange", "成本调价");
        writer.addHeaderAlias("purchaseReturn", "进退货差价");
        writer.addHeaderAlias("voucher", "代金券支出");
        writer.addHeaderAlias("saleCost", "销售成本");
        writer.addHeaderAlias("laborCost", "人力成本");
        writer.addHeaderAlias("giftCost", "赠品成本");
        writer.addHeaderAlias("revenue", "总收入");
        writer.addHeaderAlias("discount", "折让额");
        writer.addHeaderAlias("spending", "总支出");
        writer.addHeaderAlias("profit", "利润");

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
        // 获取近期不同商品的成本价
        Map<String, BigDecimal> recentPurchasePrices = productDao.findAll().stream()
                .collect(HashMap::new, (m, p)->m.put(p.getId(), p.getRecentPp()), HashMap::putAll);

        BigDecimal giftCost = BigDecimal.ZERO;
        // 审批成功的单据
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
     * 工具函数：将给定的日期字符串转换成一个Date对象
     * @param dateStr 必须是"yyyy-MM-dd HH:mm:ss"的格式
     */
    private Date parseDateStr(String dateStr) {
        int year = Integer.parseInt(dateStr.substring(0, 4));
        int month = Integer.parseInt(dateStr.substring(5, 7));
        int day = Integer.parseInt(dateStr.substring(8, 10));
        int hour = Integer.parseInt(dateStr.substring(11, 13));
        int minute = Integer.parseInt(dateStr.substring(14, 16));
        int second = Integer.parseInt(dateStr.substring(17));
        // 用Calendar来构造Date，注意Calendar中的month是从0开始计数的
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }

    /**
     * 库存中不同批次的所有的商品，与该种商品的最近进货价，计算差价
     * 由于时间的范围限制，最近进货价需要通过进货单（考虑到进货单审批完成但相应的入库单未审批完成，所以不能用入库单）来追踪
     * 而库存的历史快照需要rollback所有的出库和入库，包括出库单和入库单，进货退货和销售退货
     * 此处有一个矛盾就是，从逻辑上来说，该段时间内以某种价格买进的货品带来的盈亏理应属于该段时间，然而进货单对应的入库单可能并未审批通过
     * 如果从现在的数据库知道入库单审批成功，则可以算上该入库单中的货品，如果不知道，又需要另作处理
     * 结论：以现有的model来做太复杂了，所以只计算迄今为止，而不是任意时间段
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
//                为了实现简单，这里默认一个进货单不会分两次进相同的商品...
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
