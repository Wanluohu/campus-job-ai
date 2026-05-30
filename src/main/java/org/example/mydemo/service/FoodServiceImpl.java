package org.example.mydemo.service;

import org.example.mydemo.entity.Food;
import org.example.mydemo.other.Result;
import org.example.mydemo.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodRepository f;

    @Override
    public Result getFood(Integer id) {
        Food food = f.findFood(id);
        Result r = new Result();
        if (food == null) {
            r.setCode(404);
            r.setMsg("不存在！");
        } else {
            r.setCode(200);
            r.setMsg("成功");
            r.setData(food);
        }
        return r;
    }
}
