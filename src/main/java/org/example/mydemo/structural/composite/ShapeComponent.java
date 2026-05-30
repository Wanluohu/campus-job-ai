package org.example.mydemo.structural.composite;

/**
 * 抽象构件：定义简单积木和复合积木的公共接口
 */
public abstract class ShapeComponent {
    protected String name;

    public ShapeComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void draw();

    public void add(ShapeComponent component) {
        throw new UnsupportedOperationException("不支持添加操作");
    }

    public void remove(ShapeComponent component) {
        throw new UnsupportedOperationException("不支持移除操作");
    }

    public ShapeComponent getChild(int index) {
        throw new UnsupportedOperationException("不支持获取子节点操作");
    }
}
