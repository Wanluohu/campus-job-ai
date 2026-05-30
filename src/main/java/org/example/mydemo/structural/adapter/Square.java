package org.example.mydemo.structural.adapter;

public class Square implements IShape {
    private String name;

    public Square(String name) {
        this.name = name;
    }

    @Override
    public void draw() {
        System.out.println("绘制正方形积木：" + name);
    }
}
