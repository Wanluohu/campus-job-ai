package org.example.mydemo.repository;

import org.apache.ibatis.annotations.Mapper;
import org.example.mydemo.entity.Customer;

import java.util.List;

@Mapper
public interface CustomerRepository {
    Customer findById(Integer id);

    List<Customer> queryAll();

    int insertOne(Customer customer);

    int updateById(Integer id, String address);

    int deleteById(Integer id);
}
