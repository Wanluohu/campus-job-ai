package org.example.mydemo.structural.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * 容器构件：复合积木，可以包含简单积木或其他复合积木
 */
public class CompositeShape extends ShapeComponent {
    private List<ShapeComponent> children = new ArrayList<>();

    public CompositeShape(String name) {
        super(name);
    }

    @Override
    public void draw() {
        System.out.println("开始绘制复合积木：" + name);
        for (ShapeComponent child : children) {
            child.draw();
        }
        System.out.println("完成绘制复合积木：" + name);
    }

    @Override
    public void add(ShapeComponent component) {
        children.add(component);
    }

    @Override
    public void remove(ShapeComponent component) {
        children.remove(component);
    }

    @Override
    public ShapeComponent getChild(int index) {
        return children.get(index);
    }

    public int getChildCount() {
        return children.size();
    }
}
