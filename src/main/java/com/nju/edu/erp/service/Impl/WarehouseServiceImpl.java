package com.nju.edu.erp.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.nju.edu.erp.dao.*;
import com.nju.edu.erp.enums.sheetState.WarehouseInputSheetState;
import com.nju.edu.erp.enums.sheetState.WarehouseOutputSheetState;
import com.nju.edu.erp.model.po.*;
import com.nju.edu.erp.model.vo.ProductInfoVO;
import com.nju.edu.erp.model.vo.UserVO;
import com.nju.edu.erp.model.vo.warehouse.*;
import com.nju.edu.erp.service.ProductService;
import com.nju.edu.erp.service.WarehouseService;
import com.nju.edu.erp.utils.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final ProductDao productDao; // 其实不建议直接访问其他服务的dao层
    private final WarehouseDao warehouseDao; // 库存的 dao
    private final WarehouseInputSheetDao warehouseInputSheetDao; // 库存入库单的 dao
    private final WarehouseOutputSheetDao warehouseOutputSheetDao; // 库存出库单的 dao
    private final ProductService productService; // 产品的 servive implementation


    @Autowired
    public WarehouseServiceImpl(ProductDao productDao, WarehouseDao warehouseDao, WarehouseInputSheetDao warehouseInputSheetDao, WarehouseOutputSheetDao warehouseOutputSheetDao, ProductService productService) {
        this.productDao = productDao;
        this.warehouseDao = warehouseDao;
        this.warehouseInputSheetDao = warehouseInputSheetDao;
        this.warehouseOutputSheetDao = warehouseOutputSheetDao;
        this.productService = productService;
    }

    /**
     * 制定商品入库单
     * @param warehouseInputFormVO 入库单VO，前端传入的
     */
    @Override
    @Transactional
    public void productWarehousing(WarehouseInputFormVO warehouseInputFormVO) {
        /**
         * 商品入库
         * 1. 查看上一次入库单
         * 2. 根据上一次入库单来创建新入库单(单号/批次号/...)
         * 3. 更新"商品表", 插入"入库单表", 插入"入库单物品列表"表, 插入"库存表" -> 部分步骤放在了审批后..
         */
        // 获取上一次的入库单，特别是上一次入库单的单号、批次号
        WarehouseInputSheetPO warehouseInputSheetPO =  warehouseInputSheetDao.getLatest();
        if(warehouseInputSheetPO == null) {
            warehouseInputSheetPO = WarehouseInputSheetPO.builder().batchId(-1).build();
        }
        WarehouseInputSheetPO toSave = new WarehouseInputSheetPO();
        // 将上一次的入库单的id和批次设为本次入库单的id和批次
        toSave.setId(generateWarehouseInputId(warehouseInputSheetPO.getId(), warehouseInputSheetPO.getBatchId()));
        toSave.setBatchId(generateBatchId(warehouseInputSheetPO.getBatchId()));
//        toSave.setOperator(warehouseInputFormVO.getOperator());
        toSave.setCreateTime(new Date()); // 入库单的创建时间就是现在的时间
        // 设置关联的进货单据
        toSave.setPurchaseSheetId(warehouseInputFormVO.getPurchaseSheetId());
        toSave.setState(WarehouseInputSheetState.DRAFT);

        // 入库单每行内容的PO
        List<WarehouseInputSheetContentPO> warehouseInputListPOSheetContent = new ArrayList<>();
//        List<WarehousePO> warehousePOList = new ArrayList<>();
        warehouseInputFormVO.getList().forEach(item -> {
            // 用商品编号找到商品
            ProductPO productPO = productDao.findById(item.getPid());
//            productPO.setQuantity(productPO.getQuantity()+item.getQuantity());
//            productPO.setRecentPp(item.getPurchasePrice());
//            productDao.updateById(productPO);

            BigDecimal purchasePrice = item.getPurchasePrice();
            if(purchasePrice == null) {
                // 这次的入库单没有商品进价就用原来的商品进价
                purchasePrice = productPO.getPurchasePrice();
            }
            WarehouseInputSheetContentPO warehouseInputSheetContentPO = WarehouseInputSheetContentPO.builder()
                    .wiId(toSave.getId()) // 入库单编号
                    .pid(item.getPid()) // 商品编号
                    .quantity(item.getQuantity()) // 商品数量
                    .purchasePrice(purchasePrice) // 进价
                    .productionDate(item.getProductionDate()) // 生产日期
                    .remark(item.getRemark()).build(); // 备注
            warehouseInputListPOSheetContent.add(warehouseInputSheetContentPO);
//            WarehousePO warehousePO = WarehousePO.builder()
//                    .pid(item.getPid())
//                    .quantity(item.getQuantity())
//                    .purchasePrice(purchasePrice)
//                    .batchId(toSave.getBatchId())
//                    .productionDate(item.getProductionDate()).build();
//            warehousePOList.add(warehousePO);
        } );

        warehouseInputSheetDao.save(toSave);
        warehouseInputSheetDao.saveBatch(warehouseInputListPOSheetContent);
//        warehouseDao.saveBatch(warehousePOList);
    }

    @Override
    @Transactional
    public void productOutOfWarehouse(WarehouseOutputFormVO warehouseOutputFormVO) {
        // TODO 需要进行修改？？？
        /**
         * 商品出库
         * 1. 查到上一次出库单的ID
         * 2. 根据上一次出库单来创建新出库单
         * 3. 更新"ware..output" "ware..list_output" "warehouse" 和 "product"表
         * 逻辑跟创建入库单相似,
         *      区别有：     1. 出库单的单号需要换种方式计算
         *                 2. 批次是从前端传进来的
         *                 3. 对于warehouse表采取批量更新而不是批量新增操作
         */
        WarehouseOutputSheetPO warehouseOutputSheetPO = warehouseOutputSheetDao.getLatest();
        WarehouseOutputSheetPO toSave = new WarehouseOutputSheetPO();
        toSave.setId(generateWarehouseOutputId(warehouseOutputSheetPO == null ? null : warehouseOutputSheetPO.getId()));
        // toSave.setOperator(warehouseOutputFormVO.getOperator());
        toSave.setCreateTime(new Date());
        toSave.setSaleSheetId(warehouseOutputFormVO.getSaleSheetId());
        toSave.setState(WarehouseOutputSheetState.DRAFT);

        List<WarehouseOutputSheetContentPO> warehouseOutputListPOSheetContent = new ArrayList<>();
        warehouseOutputFormVO.getList().forEach(item -> {
            ProductPO productPO = productDao.findById(item.getPid());
//            productPO.setQuantity(productPO.getQuantity()-item.getQuantity());
//            productDao.updateById(productPO);
            BigDecimal salePrice = item.getSalePrice();
            if(salePrice == null) {
                salePrice = productPO.getRetailPrice();
            }

            WarehouseOutputSheetContentPO warehouseOutputSheetContentPO = WarehouseOutputSheetContentPO.builder()
                    .woId(toSave.getId())
                    .pid(item.getPid())
                    .quantity(item.getQuantity())
                    .salePrice(salePrice)
                    .batchId(item.getBatchId())
                    .remark(item.getRemark()).build();
            warehouseOutputListPOSheetContent.add(warehouseOutputSheetContentPO);
//            WarehousePO warehousePO = WarehousePO.builder()
//                    .pid(item.getPid())
//                    .batchId(item.getBatchId())
//                    .quantity(item.getQuantity())
//                    .build();
//            warehouseDao.deductQuantity(warehousePO);
        } );

        warehouseOutputSheetDao.save(toSave);
        warehouseOutputSheetDao.saveBatch(warehouseOutputListPOSheetContent);
    }

    /**
     * 获取出库单新单号
     * @param id 上一次的出库单单号
     * @return 新的出库单单号
     */
    private String generateWarehouseOutputId(String id) { // "CKD-20220216-00000"
        return IdGenerator.generateSheetId(id, "CKD");
    }

    @Override
    @Transactional
    public List<WarehouseOneProductInfoVO> getWareProductInfo(GetWareProductInfoParamsVO params) {
        /** params
         *  pid      要出库的商品编号
         *  quantity 要出库的商品数量
         *  remark   备注
        */
        /**
         * 这是商品出库的前驱步骤 ——
         * 先查对应商品的批次, 把不同批次的商品按照入库的时间顺序取出
         * [比如有三批相同商品,分别进了100个,现在出库需要150个,那现在取第一批的100个和第二批的50个],
         * 然后返回给前端。
         * 前端可以继续选择不同的商品出库, 最终各种批次的各种商品组织成list作为出库单的参数
         */
        int quantity = params.getQuantity();
        List<WarehousePO> warehousePOS = warehouseDao.findAllNotZeroByPidSortedByBatchId(params.getPid());
        List<WarehouseOneProductInfoVO> res = new ArrayList<>();
        for (int i = 0; i < warehousePOS.size() && quantity > 0; i++) {
            WarehousePO warehousePO = warehousePOS.get(i);
            WarehouseOneProductInfoVO resItem = WarehouseOneProductInfoVO.builder()
                    .productId(warehousePO.getPid())
                    .batchId(warehousePO.getBatchId())
                    .purchasePrice(warehousePO.getPurchasePrice())
                    .totalQuantity(warehousePO.getQuantity())
                    .remark(params.getRemark())
                    .build();
            if (warehousePO.getQuantity() <= quantity) {
                // 这个批次的商品的数量小于要出库的数量
                resItem.setSelectedQuantity(warehousePO.getQuantity());
                resItem.setSumPrice(warehousePO.getPurchasePrice().multiply(BigDecimal.valueOf(warehousePO.getQuantity())));
                quantity -= warehousePO.getQuantity();
            } else {
                resItem.setSelectedQuantity(quantity);
                resItem.setSumPrice(warehousePO.getPurchasePrice().multiply(BigDecimal.valueOf(quantity)));
                quantity = 0;
            }
            res.add(resItem);
        }
        return res;
    }

    /**
     * 审批入库单(仓库管理员进行确认/总经理进行审批)
     *
     * @param warehouseInputSheetId 入库单id
     * @param state                 入库单修改后的状态(state == "待审批"/"审批失败"/"审批完成")
     */
    @Override
    @Transactional
    public void approvalInputSheet(UserVO user, String warehouseInputSheetId, WarehouseInputSheetState state) {
        // TODO
        // 也许要加一个修改草稿的接口 此处只是审批通过并修改操作员
        WarehouseInputSheetPO warehouseInputSheetPO = warehouseInputSheetDao.getSheet(warehouseInputSheetId);
        warehouseInputSheetPO.setOperator(user.getName());
        warehouseInputSheetPO.setState(state);
        // 在数据库中更新单据状态
        warehouseInputSheetDao.updateById(warehouseInputSheetPO);
        // 获取对应的商品 更新仓库相关数据
        List<WarehouseInputSheetContentPO> productsList = warehouseInputSheetDao.getAllContentById(warehouseInputSheetId);
        List<WarehousePO> warehousePOList = new ArrayList<>();
        for (WarehouseInputSheetContentPO product : productsList) {
            ProductPO productPO = productDao.findById(product.getPid());
            // 更新最新数量
            productPO.setQuantity(productPO.getQuantity() + product.getQuantity());
            productDao.updateById(productPO);
            // 更新库存信息
            WarehousePO warehousePO = WarehousePO.builder()
                    .pid(product.getPid())
                    .quantity(product.getQuantity())
                    .purchasePrice(product.getPurchasePrice())
                    .batchId(warehouseInputSheetPO.getBatchId())
                    .productionDate(product.getProductionDate()).build();
            warehousePOList.add(warehousePO);
        }
        warehouseDao.saveBatch(warehousePOList);
    }

    /**
     * 通过状态获取入库单(state == null 时获取全部入库单)
     *
     * @param state 入库单状态
     * @return 入库单
     */
    @Override
    public List<WarehouseInputSheetPO> getWareHouseInputSheetByState(WarehouseInputSheetState state) {
        if (state == null) {
            return warehouseInputSheetDao.getAllSheets();
        }
        else {
            return warehouseInputSheetDao.getDraftSheets(state);
        }
    }

    // 根据单据状态获取库存出库单
    @Override
    public List<WarehouseOutputSheetPO> getWareHouseOutSheetByState(WarehouseOutputSheetState state) {
        if (state == null) {
            return warehouseOutputSheetDao.getAllSheets();
        }
        else {
            return warehouseOutputSheetDao.getDraftSheets(state);
        }
    }

    /**
     * 审批出库单
     * @param user
     * @param sheetId 出库单id
     * @param state 出库单修改后的状态(state == "审批失败"/"审批完成")
     */
    @Override
    @Transactional
    public void approvalOutputSheet(UserVO user, String sheetId, WarehouseOutputSheetState state) {
        WarehouseOutputSheetPO warehouseOutputSheetPO = warehouseOutputSheetDao.getSheet(sheetId);
        warehouseOutputSheetPO.setOperator(user.getName());
        warehouseOutputSheetPO.setState(state);
        warehouseOutputSheetDao.updateById(warehouseOutputSheetPO);
        // 获取对应的商品 更新仓库相关数据
        List<WarehouseOutputSheetContentPO> productsList = warehouseOutputSheetDao.getAllContentById(sheetId);
        // 删除原有的不含批次的content
        warehouseOutputSheetDao.deleteContent(sheetId);
        // 分配后的出库单
        List<WarehouseOutputSheetContentPO> ans = new ArrayList<>();

        for (WarehouseOutputSheetContentPO product : productsList) {
            ProductPO productPO = productDao.findById(product.getPid());
            // 更新最新数量
            productPO.setQuantity(productPO.getQuantity() - product.getQuantity());
            productDao.updateById(productPO);
            // 更新库存信息
            // ?如何出货
            // 查询获取同一商品的不同批次信息 按进价排序
            int remainAmount = product.getQuantity();
            List<WarehousePO> availableWarehouses = warehouseDao.findByPidOrderByPurchasePricePos(product.getPid());
            for (WarehousePO availableWarehouse : availableWarehouses) {
                WarehouseOutputSheetContentPO warehouseOutputSheetContentPO = new WarehouseOutputSheetContentPO();
                BeanUtils.copyProperties(product, warehouseOutputSheetContentPO);
                if (availableWarehouse.getQuantity() >= remainAmount) {
                    availableWarehouse.setQuantity(remainAmount);
                    warehouseDao.deductQuantity(availableWarehouse);
                    warehouseOutputSheetContentPO.setBatchId(availableWarehouse.getBatchId());
                    ans.add(warehouseOutputSheetContentPO);
                    break;
                }
                else {
                    remainAmount = remainAmount - availableWarehouse.getQuantity();
                    warehouseDao.deductQuantity(availableWarehouse);
                    warehouseOutputSheetContentPO.setBatchId(availableWarehouse.getBatchId());
                    ans.add(warehouseOutputSheetContentPO);
                }
            }
        }
        warehouseOutputSheetDao.saveBatch(ans);
    }

    @Override
    public List<WarehouseInputSheetContentPO> getWHISheetContentById(String sheetId) {
        return warehouseInputSheetDao.getAllContentById(sheetId);
    }

    @Override
    public List<WarehouseOutputSheetContentPO> getWHOSheetContentById(String sheetId) {
        return warehouseOutputSheetDao.getAllContentById(sheetId);
    }


    /**
     * 获取新批次
     * @param batchId 批次
     * @return 新批次
     */
    private Integer generateBatchId(Integer batchId) {
        return batchId + 1;
    }

    /**
     * 获取新入库单单号
     * @param id 入库单单号
     * @return 新入库单单号
     */
    private String generateWarehouseInputId(String id, Integer batchId) { // "RKD-20220216-00000"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = dateFormat.format(new Date());
        if(batchId == -1) {
            return "RKD-" + today + "-" + String.format("%05d", 0);
        }
        String lastDate = id.split("-")[1];
        if(lastDate.equals(today)) {
            return "RKD-" + today + "-" + String.format("%05d", batchId + 1);
        } else {
            return "RKD-" + today + "-" + String.format("%05d", 0);
        }
    }

    /**
     * 库存查看：设定一个时间段，查看此时间段内的出/入库数量/金额/商品信息/分类信息
     * @param beginDateStr 开始时间字符串 格式为："yyyy-MM-dd HH:mm:ss"
     * @param endDateStr 结束时间字符串  格式为："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    @Override
    public List<WarehouseIODetailPO> getWarehouseIODetailByTime(String beginDateStr,String endDateStr) {
        // todo HPY DONE
        /**
         * 1.注意日期的格式转换和转换异常
         * 2.考虑开始时间大于结束时间的情况、查询结果为空的情况
         * 3.Dao层和service层接口已实现
         *
         */
        Date beginDate = parseDateStr(beginDateStr);
        Date endDate = parseDateStr(endDateStr);
        if (beginDate.after(endDate)) {
            // 开始时间在结束时间之后，返回null
            return null;
        }
        List<WarehouseIODetailPO> ans = new ArrayList<>();
        List<WarehouseIODetailPO> output = warehouseOutputSheetDao.getWarehouseIODetailByTime(beginDate, endDate);
        List<WarehouseIODetailPO> input = warehouseInputSheetDao.getWarehouseIODetailByTime(beginDate, endDate);
        for (WarehouseIODetailPO each : output) {
            ans.add(each);
        }
        for (WarehouseIODetailPO each : input) {
            ans.add(each);
        }
        return ans;
    }

    /**
     * 库存查看：一个时间段内的入库数量合计
     * @param beginDateStr 开始时间字符串 格式为："yyyy-MM-dd HH:mm:ss"
     * @param endDateStr 结束时间字符串 格式为："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public int getWarehouseInputProductQuantityByTime(String beginDateStr,String endDateStr){
        // todo HPY Done
        /**
         * 1.注意日期的格式转换和转换异常
         * 2.考虑开始时间大于结束时间的情况、查询结果为空的情况
         * 3.Dao层和service层接口已实现，方法对应的Mapper为WarehouseInputSheetMapper
         */
        Date beginDate = parseDateStr(beginDateStr);
        Date endDate = parseDateStr(endDateStr);
        if (beginDate.after(endDate)) {
            // 开始时间在结束时间之后，返回null
            return 0;
        }
        Integer res = warehouseInputSheetDao.getWarehouseInputProductQuantityByTime(beginDate, endDate);
        if (res == null) {
            return 0;
        }
        return res;
    }

    /**
     * 库存查看：一个时间段内的出库数量合计
     * @param beginDateStr 开始时间字符串 格式为："yyyy-MM-dd HH:mm:ss"
     * @param endDateStr 结束时间字符串 格式为："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public int getWarehouseOutProductQuantityByTime(String beginDateStr,String endDateStr){
        // todo HPY Done
        /**
         * 1.注意日期的格式转换和转换异常
         * 2.考虑开始时间大于结束时间的情况、查询结果为空的情况
         * 3.Dao层和service层接口已提供，需要先补充WarehouseInputSheetMapper中的sql语句
         */
        Date beginDate = parseDateStr(beginDateStr);
        Date endDate = parseDateStr(endDateStr);
        if (beginDate.after(endDate)) {
            // 开始时间在结束时间之后，返回null
            return 0;
        }
        Integer res = warehouseOutputSheetDao.getWarehouseOutputProductQuantityByTime(beginDate, endDate);
        if (res == null) {
            return 0;
        }
        return res;
    }

    /**
     * 将给定的日期字符串转换成一个Date对象
     * @param dateStr 必须是"yyyy-MM-dd HH:mm:ss"的格式
     * @return
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
     * 库存盘点
     * 盘点的是当天的库存快照，包括当天的各种商品的
     * 名称，型号，库存数量，库存均价，批次，批号，出厂日期，并且显示行号。
     * 要求可以导出Excel
     */
    @Override
    public List<WarehouseCountingVO> warehouseCounting() {
        List<WarehousePO> all = warehouseDao.findAll();
        List<WarehouseCountingVO> res = new ArrayList<>();
        for(WarehousePO warehousePO : all) {
            WarehouseCountingVO vo = new WarehouseCountingVO();
            BeanUtils.copyProperties(warehousePO, vo);
            String pid = warehousePO.getPid();
            ProductInfoVO product = productService.getOneProductByPid(pid);
            vo.setProduct(product);
            res.add(vo);
        }
        return res;
    }

    @Override
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<WarehousePO> all = warehouseDao.findAll();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 定义标题名
        writer.addHeaderAlias("id","库存id");
        writer.addHeaderAlias("pid","商品编号");
        writer.addHeaderAlias("quantity","商品数量");
        writer.addHeaderAlias("purchasePrice","进货价格");
        writer.addHeaderAlias("batchId","批次号码");
        writer.addHeaderAlias("productionDate","出厂日期");
        writer.write(all, true);
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
}
