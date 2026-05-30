package org.example.mydemo.service;

import org.example.mydemo.entity.Customer;
import org.example.mydemo.other.Result;

import java.util.List;

public interface CustomerService {
    Result getCustomer(Integer id);

    List<Customer> getAllCustomers();

    Result addCustomer(Customer customer);

    Result updateCustomer(Integer id, String address);

    Result deleteCustomer(Integer id);
}
