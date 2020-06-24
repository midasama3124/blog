package com.mmanchola.blog.security;

import com.google.common.collect.Sets;
import java.util.Set;

public enum ApplicationUserRole {
  READER(Sets.newHashSet(ApplicationUserPermission.POST_READ)),
  ADMIN(Sets.newHashSet(ApplicationUserPermission.POST_READ, ApplicationUserPermission.POST_WRITE));

  private final Set<ApplicationUserPermission> permissions;

  ApplicationUserRole(
      Set<ApplicationUserPermission> permissions) {
    this.permissions = permissions;
  }
}
