package org.example.mydemo.structural.composite;

/**
 * 叶子构件：简单积木（如三角形、圆形等）
 */
public class SimpleShape extends ShapeComponent {
    private String shapeType;

    public SimpleShape(String name, String shapeType) {
        super(name);
        this.shapeType = shapeType;
    }

    @Override
    public void draw() {
        System.out.println("  绘制简单积木[" + shapeType + "]：" + name);
    }
}
