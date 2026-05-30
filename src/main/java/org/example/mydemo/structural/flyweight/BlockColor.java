package org.example.mydemo.structural.flyweight;

/**
 * 享元类：积木颜色，相同颜色的对象可共享
 */
public class BlockColor {
    private String colorName;

    public BlockColor(String colorName) {
        this.colorName = colorName;
    }

    public String getColorName() {
        return colorName;
    }

    public void applyColor(String shapeName) {
        System.out.println("  为积木[" + shapeName + "]应用颜色：" + colorName
                + "（颜色对象：" + this.hashCode() + "）");
    }
}
