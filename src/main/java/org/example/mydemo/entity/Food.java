package org.example.mydemo.entity;

import lombok.Data;

@Data
public class Food {
    private Integer id;
    private String name;
    private String type;
    private String state;
    private String price;
    private String quantity;
}
