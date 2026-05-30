package org.example.mydemo.structural.adapter;

/**
 * 第三方矩形类，接口与IShape不兼容
 */
public class Rectangle {
    private String name;
    private RectangleAdapter adapter;

    public Rectangle(String name) {
        this.name = name;
    }

    public void display() {
        System.out.println("显示第三方矩形：" + name);
    }

    public String getName() {
        return name;
    }

    public void setAdapter(RectangleAdapter adapter) {
        this.adapter = adapter;
    }

    public RectangleAdapter getAdapter() {
        return adapter;
    }
}
