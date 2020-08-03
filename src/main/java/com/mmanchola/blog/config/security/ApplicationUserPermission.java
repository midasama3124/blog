package com.mmanchola.blog.config.security;

public enum ApplicationUserPermission {
  POST_READ("post:read"),
  POST_WRITE("post:write");

  private final String permission;

  ApplicationUserPermission(String permission) {
    this.permission = permission;
  }

  public String getPermission() {
    return permission;
  }
}
