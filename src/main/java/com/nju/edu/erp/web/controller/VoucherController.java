package com.nju.edu.erp.web.controller;

import com.nju.edu.erp.dao.VoucherDao;
import com.nju.edu.erp.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Autowired
    private VoucherDao voucherDao;

    @GetMapping("/all")
    public Response getAll(@RequestParam("id") Integer id) {
        return Response.buildSuccess(voucherDao.getAllByCustomerId(id));
    }

}
