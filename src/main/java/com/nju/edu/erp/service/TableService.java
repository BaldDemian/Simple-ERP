package com.nju.edu.erp.service;

import com.nju.edu.erp.model.vo.finance.CashSheetVO;
import com.nju.edu.erp.model.vo.finance.CollectSheetVO;
import com.nju.edu.erp.model.vo.finance.PaySheetVO;
import com.nju.edu.erp.model.vo.finance.SalarySheetVO;
import com.nju.edu.erp.model.vo.table.BusinessHistoryVO;
import com.nju.edu.erp.model.vo.table.SaleDetailsCondVO;
import com.nju.edu.erp.model.vo.table.SaleDetailsVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface TableService {

     Map<String, List<SaleDetailsVO>> getSaleDetailsTable(SaleDetailsCondVO saleDetailsCondVO);

    void getSaleDetailsTableExcel(HttpServletResponse response, SaleDetailsCondVO saleDetailsCondVO) throws IOException;

    BusinessHistoryVO getBusinessHistory();

    void getBusinessHistoryExcel(HttpServletResponse response) throws IOException;

    List<CollectSheetVO> getTargetCollectSheet(String beginDateStr, String endDateStr, String customer, String operator);
    List<PaySheetVO> getTargetPaySheet(String beginDateStr, String endDateStr, String customer, String operator);
    List<CashSheetVO> getTargetCashSheet(String beginDateStr, String endDateStr, String operator);
    List<SalarySheetVO> getTargetSalarySheet(String beginDateStr, String endDateStr);
}
