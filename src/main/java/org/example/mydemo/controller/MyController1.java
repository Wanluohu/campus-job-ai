package org.example.mydemo.controller;

import org.example.mydemo.entity.Food;
import org.example.mydemo.other.Result;
import org.example.mydemo.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MyController1 {

    @Autowired
    private FoodService foodService;

    @GetMapping("/index")
    public String init(Model model) {
        Result result = new Result();
        result.setCode(100);
        result.setMsg("初始状态");
        model.addAttribute("result", result);
        return "index";
    }

    @PostMapping("/check")
    public String loginCheck(Model model, Integer id) {
        Result result = foodService.getFood(id);
        model.addAttribute("result", result);
        if (result.getCode() == 200) {
            Food f = (Food) result.getData();
            model.addAttribute("id", f.getId());
            model.addAttribute("name", f.getName());
            model.addAttribute("type", f.getType());
            model.addAttribute("state", f.getState());
            model.addAttribute("price", f.getPrice());
            model.addAttribute("quantity", f.getQuantity());
            return "home";
        } else {
            model.addAttribute("id", id);
            return "index";
        }
    }
}
