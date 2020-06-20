package com.mmanchola.blog.security;

import static com.mmanchola.blog.security.ApplicationUserPermission.POST_READ;
import static com.mmanchola.blog.security.ApplicationUserPermission.POST_WRITE;

import com.google.common.collect.Sets;
import java.util.Set;

public enum ApplicationUserRole {
  READER(Sets.newHashSet(POST_READ)),
  ADMIN(Sets.newHashSet(POST_READ, POST_WRITE));

  private final Set<ApplicationUserPermission> permissions;

  ApplicationUserRole(
      Set<ApplicationUserPermission> permissions) {
    this.permissions = permissions;
  }
}
