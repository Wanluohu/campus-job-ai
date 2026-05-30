package org.example.mydemo.controller;

import org.example.mydemo.entity.Customer;
import org.example.mydemo.other.Result;
import org.example.mydemo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 客户信息列表页面
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customerList", customers);
        return "customer/list";
    }

    /**
     * 按编号查询客户
     */
    @GetMapping("/query")
    public String queryPage() {
        return "customer/query";
    }

    @PostMapping("/query")
    public String query(Model model, Integer id) {
        Result result = customerService.getCustomer(id);
        model.addAttribute("result", result);
        if (result.getCode() == 200) {
            model.addAttribute("customer", result.getData());
        }
        return "customer/query";
    }

    /**
     * 添加客户页面
     */
    @GetMapping("/add")
    public String addPage() {
        return "customer/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public Result add(Customer customer) {
        return customerService.addCustomer(customer);
    }

    /**
     * 修改客户页面（按编号修改地址）
     */
    @GetMapping("/edit")
    public String editPage() {
        return "customer/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public Result edit(Integer id, String address) {
        return customerService.updateCustomer(id, address);
    }

    /**
     * 删除客户页面
     */
    @GetMapping("/delete")
    public String deletePage() {
        return "customer/delete";
    }

    @PostMapping("/delete")
    @ResponseBody
    public Result delete(Integer id) {
        return customerService.deleteCustomer(id);
    }
}
