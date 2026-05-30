package org.example.mydemo.structural.proxy;

import org.example.mydemo.structural.adapter.IShape;

/**
 * 代理类：在不修改原有积木类的基础上，增加权限控制功能
 */
public class ShapeProxy implements IShape {
    private IShape realShape;
    private String shapeName;

    public ShapeProxy(IShape realShape, String shapeName) {
        this.realShape = realShape;
        this.shapeName = shapeName;
    }

    @Override
    public void draw() {
        if (PermissionService.hasPermission(shapeName)) {
            System.out.println("  [代理] 权限验证通过，开始搭建积木...");
            realShape.draw();
        } else {
            System.out.println("  [代理] 权限不足，无法搭建积木[" + shapeName + "]");
        }
    }
}
