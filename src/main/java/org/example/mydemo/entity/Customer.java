package org.example.mydemo.entity;

import lombok.Data;

@Data
public class Customer {
    private Integer id;
    private String name;
    private String gender;
    private Integer age;
    private String address;
    private String phone;
}
