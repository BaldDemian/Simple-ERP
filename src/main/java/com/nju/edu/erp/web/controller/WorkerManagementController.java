package com.nju.edu.erp.web.controller;

import com.nju.edu.erp.auth.Authorized;
import com.nju.edu.erp.enums.Role;
import com.nju.edu.erp.model.vo.workerManagement.WorkerVO;
import com.nju.edu.erp.service.WorkerManagementService;
import com.nju.edu.erp.web.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/workManagement")
public class WorkerManagementController {

    private final WorkerManagementService workerManagementService;

    @Autowired
    public WorkerManagementController(WorkerManagementService workerManagementService) {
        this.workerManagementService = workerManagementService;
    }

    @Authorized(roles = {Role.HR,Role.ADMIN})
    @PostMapping("/worker/add")
    public Response addWorker(@RequestBody WorkerVO workerVO){
        workerManagementService.addWorker(workerVO);
        return Response.buildSuccess();
    }

    @Authorized(roles = {Role.HR,Role.ADMIN})
    @GetMapping("/query-all")
    public Response queryAll(){
        return Response.buildSuccess(workerManagementService.queryAll());
    }
}
