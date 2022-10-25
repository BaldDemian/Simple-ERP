package com.nju.edu.erp.model.po.sheets;

import com.nju.edu.erp.model.po.SheetPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalarySheetPO extends SheetPO {
    /**
     * 员工编号
     */
    private String employeeId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 银行账户名
     */
    private String account;
    /**
     * 应发工资
     */
    private BigDecimal rawSalary;
    /**
     * 扣除的税款
     */
    private BigDecimal tax;
    /**
     * 实际工资
     */
    private BigDecimal actualSalary;
}
