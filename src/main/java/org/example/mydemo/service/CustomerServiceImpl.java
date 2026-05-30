package org.example.mydemo.service;

import org.example.mydemo.entity.Customer;
import org.example.mydemo.other.Result;
import org.example.mydemo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Result getCustomer(Integer id) {
        Customer customer = customerRepository.findById(id);
        Result r = new Result();
        if (customer == null) {
            r.setCode(404);
            r.setMsg("客户不存在！");
        } else {
            r.setCode(200);
            r.setMsg("查询成功");
            r.setData(customer);
        }
        return r;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.queryAll();
    }

    @Override
    public Result addCustomer(Customer customer) {
        Result r = new Result();
        int rows = customerRepository.insertOne(customer);
        if (rows == 1) {
            r.setCode(200);
            r.setMsg("添加客户成功！");
        } else {
            r.setCode(500);
            r.setMsg("添加客户失败！");
        }
        return r;
    }

    @Override
    public Result updateCustomer(Integer id, String address) {
        Result r = new Result();
        int rows = customerRepository.updateById(id, address);
        if (rows == 1) {
            r.setCode(200);
            r.setMsg("更新客户地址成功！");
        } else {
            r.setCode(500);
            r.setMsg("更新失败，客户不存在！");
        }
        return r;
    }

    @Override
    public Result deleteCustomer(Integer id) {
        Result r = new Result();
        int rows = customerRepository.deleteById(id);
        if (rows == 1) {
            r.setCode(200);
            r.setMsg("删除客户成功！");
        } else {
            r.setCode(500);
            r.setMsg("删除失败，客户不存在！");
        }
        return r;
    }
}
