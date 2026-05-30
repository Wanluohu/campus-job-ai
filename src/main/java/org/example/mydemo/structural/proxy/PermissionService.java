package org.example.mydemo.structural.proxy;

/**
 * 权限服务：控制积木是否能被搭建
 */
public class PermissionService {

    public static boolean hasPermission(String shapeName) {
        // 模拟权限控制：名称包含"禁用"的积木没有权限
        if (shapeName.contains("禁用")) {
            System.out.println("  [权限检查] 积木[" + shapeName + "]：搭建权限不足，禁止操作！");
            return false;
        }
        System.out.println("  [权限检查] 积木[" + shapeName + "]：权限验证通过");
        return true;
    }
}
