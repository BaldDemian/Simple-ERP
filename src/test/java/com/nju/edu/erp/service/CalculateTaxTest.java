package com.nju.edu.erp.service;

import com.nju.edu.erp.service.Impl.FinanceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
/**
 * 计算个人所得税的单元测试（黑盒测试）
 */
public class CalculateTaxTest {
    @Autowired
    FinanceServiceImpl financeServiceImpl;

    // 边界测试
    @Test
    public void calculateTaxTest() {
        double actual = financeServiceImpl.calculateTax(5000);
        Assertions.assertEquals(0.0, actual);
    }
    // 边界测试
    @Test
    public void calculateTaxTest1() {
        double actual = financeServiceImpl.calculateTax(80000);
        Assertions.assertEquals(19090.0, actual);
    }
    // 随机测试
    @Test
    public void calculateTaxTest2() {
        double actual = financeServiceImpl.calculateTax(84513);
        Assertions.assertEquals(20669.55, actual);
    }
    // 随机测试
    @Test
    public void calculateTaxTest3() {
        double actual = financeServiceImpl.calculateTax(35555);
        Assertions.assertEquals(4978.75, actual);
    }
}
