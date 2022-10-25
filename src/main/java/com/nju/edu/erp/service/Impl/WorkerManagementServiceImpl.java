package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.CardDao;
import com.nju.edu.erp.dao.WorkerManagementDao;
import com.nju.edu.erp.model.po.WorkerPO;
import com.nju.edu.erp.model.vo.workerManagement.WorkerVO;
import com.nju.edu.erp.service.WorkerManagementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WorkerManagementServiceImpl implements WorkerManagementService {

    private final WorkerManagementDao workerManagementDao;
    private final CardDao cardDao;

    @Autowired
    public WorkerManagementServiceImpl(WorkerManagementDao workerManagementDao, CardDao cardDao) {
        this.workerManagementDao = workerManagementDao;
        this.cardDao = cardDao;
    }


    /**
     * 添加员工信息
     *
     * @param workerVO 员工信息
     */
    @Override
    public void addWorker(WorkerVO workerVO) {
        WorkerPO workerPO = new WorkerPO();
        BeanUtils.copyProperties(workerVO,workerPO);

        workerManagementDao.addWorker(workerPO);
        cardDao.addWorker(workerManagementDao.getLatestId());
    }

    @Override
    public List<WorkerVO> queryAll() {
        List<WorkerVO> res=new ArrayList<>();
        List<WorkerPO> all=workerManagementDao.queryAll();
        for (WorkerPO po:all){
            WorkerVO vo=new WorkerVO();
            BeanUtils.copyProperties(po,vo);
            res.add(vo);
        }
        return res;
    }
}