package com.nju.edu.erp.dao;

import com.nju.edu.erp.enums.sheetState.VoucherState;
import com.nju.edu.erp.model.po.VoucherPO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
public class VoucherDaoTest {

    @Autowired
    private VoucherDao voucherDao;

    @Test
    void testGetAll() {
        System.out.println(voucherDao.getAllByCustomerId(2));
    }

    @Test
    void testGetAvail() {
        System.out.println(voucherDao.getAvailByCustomerId(2));
    }

    @Test
    @Transactional
    @Rollback(value = false)
    void testAdd() {
        VoucherPO voucherPO = new VoucherPO();
        voucherPO.setCustomerId(2);
        voucherPO.setValue(BigDecimal.valueOf(999));
        voucherPO.setState(VoucherState.AVAILABLE);
        voucherPO.setRemark("test add");
        voucherDao.add(voucherPO);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    void testUse() {
        voucherDao.use(3);
    }
}
