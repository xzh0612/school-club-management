package com.club.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private Integer permissionId;    // 权限ID
    private String permissionName;   // 权限名称
    private String permissionCode;   // 权限编码
    private String description;      // 描述
    private String resourceType;     // 资源类型: menu, button, api
    private String resourcePath;     // 资源路径
    private String method;           // HTTP方法
}