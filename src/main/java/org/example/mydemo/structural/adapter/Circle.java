package org.example.mydemo.structural.adapter;

public class Circle implements IShape {
    private String name;

    public Circle(String name) {
        this.name = name;
    }

    @Override
    public void draw() {
        System.out.println("绘制圆形积木：" + name);
    }
}
