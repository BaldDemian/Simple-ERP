package com.nju.edu.erp.dao;

import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.model.po.WorkerPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Mapper
public interface WorkerManagementDao {
    /**
     * 添加员工
     * @param workerPO 员工信息
     */
    void addWorker(WorkerPO workerPO);

    String findRoleById(Integer workerId);

    List<WorkerPO> queryAll();

    Integer getLatestId();
}
