package org.example.mydemo.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

/**
 * 享元工厂：管理和共享BlockColor对象
 */
public class ColorFactory {
    private static final Map<String, BlockColor> colorPool = new HashMap<>();

    public static BlockColor getColor(String colorName) {
        BlockColor color = colorPool.get(colorName);
        if (color == null) {
            color = new BlockColor(colorName);
            colorPool.put(colorName, color);
            System.out.println("  创建新颜色对象：" + colorName + "（hashCode：" + color.hashCode() + "）");
        } else {
            System.out.println("  复用已有颜色对象：" + colorName + "（hashCode：" + color.hashCode() + "）");
        }
        return color;
    }

    public static int getColorCount() {
        return colorPool.size();
    }
}
