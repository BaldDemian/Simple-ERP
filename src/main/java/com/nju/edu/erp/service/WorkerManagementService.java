package com.nju.edu.erp.service;

import com.nju.edu.erp.model.vo.workerManagement.WorkerVO;

import java.util.List;

public interface WorkerManagementService {
    void addWorker(WorkerVO workerVO);

    List<WorkerVO> queryAll();
}
