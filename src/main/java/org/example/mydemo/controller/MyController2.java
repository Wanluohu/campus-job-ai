package org.example.mydemo.controller;

import org.example.mydemo.entity.Food;
import org.example.mydemo.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MyController2 {

    @Autowired
    FoodRepository fd;

    /**
     * 根据商品ID查询食品信息
     * @param pid 商品ID，对应URL中的查询参数
     * @return 返回对应的Food对象，如果未找到则返回null
     */
    @ResponseBody
    @GetMapping("/get")
    public Food getComByPid(Integer pid) {
        return fd.findFood(pid);
    }

    /**
     * 获取全部食品信息，REST接口
     * @return 返回包含所有Food对象的列表，以JSON格式响应
     */
    @ResponseBody
    @GetMapping("/getall")
    public List<Food> getComAll() {
        return fd.queryAll();
    }

    /**
     * 获取全部食品信息，跳转至展示页面
     * @param m Spring MVC模型对象，用于向视图层传递食品列表数据
     * @return 返回"allfood"视图名称，渲染食品列表页
     */
    @GetMapping("/getall2")
    public String getComAll2(Model m) {
        List<Food> list = fd.queryAll();
        m.addAttribute("foodlist", list);
        return "allfood";
    }

    /**
     * 跳转至添加食品页面
     * @return 返回"add"视图名称，渲染添加食品的表单页
     */
    @GetMapping("/add")
    public String add() {
        return "add";
    }

    /**
     * 处理添加食品的表单提交请求
     * @param f 食品对象，包含要添加的食品详细信息
     * @return 返回操作结果提示信息，成功返回"已添加商品！"，失败返回"添加失败！"
     */
    @ResponseBody
    @PostMapping("/addform")
    public String addform(Food f) {
        return fd.insertOne(f) == 1 ? "已添加商品！" : "添加失败！";
    }

    /**
     * 跳转至修改食品信息页面
     * @return 返回"set"视图名称，渲染修改食品的表单页
     */
    @GetMapping("/set")
    public String setComByPid() {
        return "set";
    }

    /**
     * 处理修改食品库存的表单提交请求
     * @param id 食品ID，用于定位要更新的食品记录
     * @param price 新的库存数量，用于更新食品的库存信息
     * @return 返回操作结果提示信息，成功返回"已更新库存。"，失败返回"更新失败！"
     */
    @ResponseBody
    @PostMapping("/setform")
    public String setform(String id, int price) {
        return fd.updateById(id, price) == 1 ? "已更新库存。" : "更新失败！";
    }

    /**
     * 跳转至删除食品页面
     * @return 返回"del"视图名称，渲染删除食品的表单页
     */
    @GetMapping("/del")
    public String del() {
        return "del";
    }

    /**
     * 处理删除食品的表单提交请求
     * @param id 食品ID，用于定位要删除的食品记录
     * @return 返回操作结果提示信息，成功返回"已删除商品！"，失败返回"删除失败！"
     */
    @ResponseBody
    @PostMapping("/delform")
    public String delComByPid(String id) {
        return fd.deleteById(id) == 1 ? "已删除商品！" : "删除失败！";
    }
}
