package org.example.mydemo.structural.adapter;

/**
 * 对象适配器：将第三方Rectangle适配为IShape接口
 * 与Rectangle形成双向关联关系
 */
public class RectangleAdapter implements IShape {
    private Rectangle rectangle;

    public RectangleAdapter(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.rectangle.setAdapter(this);
    }

    @Override
    public void draw() {
        rectangle.display();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
