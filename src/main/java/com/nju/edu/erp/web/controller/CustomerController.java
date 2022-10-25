package com.nju.edu.erp.web.controller;

import com.nju.edu.erp.enums.CustomerType;
import com.nju.edu.erp.model.po.CustomerPO;
import com.nju.edu.erp.model.vo.CustomerVO;
import com.nju.edu.erp.service.CustomerService;
import com.nju.edu.erp.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/customer")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/findByType")
    public Response findByType(@RequestParam CustomerType type) {
        return Response.buildSuccess(customerService.getCustomersByType(type));
    }

    @PostMapping("/createCustomer")
    public Response createCustomer(@RequestBody CustomerVO customerVO) {
        customerService.createCustomer(customerVO);
        return Response.buildSuccess();
    }

    @GetMapping("/deleteCustomer")
    public Response deleteCustomer(@RequestParam Integer id) {
        int res = customerService.deleteCustomer(id);
        if (res == 0) {
            return Response.buildFailed("111", "删除失败，请检查ID是否正确");
        } else {
            return Response.buildSuccess();
        }
    }
}
