package com.nju.edu.erp.service;

import com.nju.edu.erp.dao.FinanceDao;
import com.nju.edu.erp.model.vo.finance.CollectSheetVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


/**
 * 测试查找指定单据，使用本地数据库。主要是测试条件SQL的正确性。由于使用了本地数据库，部署时应该删去本测试
 */
@SpringBootTest
public class GetTargetSheetTest {
    @Autowired
    FinanceDao financeDao;

    /**
     * 参数全为null的测试，应该筛选出所有收款单
     */
    @Test
    public void GetTargetSheetTest1() {
        List<CollectSheetVO> res = financeDao.getTargetCollectSheet(null, null, null, null);
        Assertions.assertEquals(2, res.size());
    }
    /**
     * operator 参数不为空
     */
    @Test
    public void GetTargetSheetTest2() {
        List<CollectSheetVO> res = financeDao.getTargetCollectSheet(null, null, null, "1");
        Assertions.assertEquals(1, res.size());
    }
}
