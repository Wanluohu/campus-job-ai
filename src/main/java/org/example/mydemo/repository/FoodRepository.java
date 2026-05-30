package org.example.mydemo.repository;

import org.apache.ibatis.annotations.Mapper;
import org.example.mydemo.entity.Food;

import java.util.List;

@Mapper
public interface FoodRepository {
    Food findFood(Integer id);

    List<Food> queryAll();

    int insertOne(Food food);

    int updateById(String id, int price);

    int deleteById(String id);
}
