package com.mmanchola.blog.model;

public class RolePermission {

  private final short roleId;
  private final int permissionId;

  public RolePermission(short roleId, int permissionId) {
    this.roleId = roleId;
    this.permissionId = permissionId;
  }

  public short getRoleId() {
    return roleId;
  }

  public int getPermissionId() {
    return permissionId;
  }

}
