package com.mmanchola.blog.config.security;

import static com.mmanchola.blog.config.security.ApplicationUserPermission.POST_READ;
import static com.mmanchola.blog.config.security.ApplicationUserPermission.POST_WRITE;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum ApplicationUserRole {
  READER(Sets.newHashSet(POST_READ)),
  ADMIN(Sets.newHashSet(POST_READ, POST_WRITE));

  private final Set<ApplicationUserPermission> permissions;

  ApplicationUserRole(
      Set<ApplicationUserPermission> permissions) {
    this.permissions = permissions;
  }

  public Set<ApplicationUserPermission> getPermissions () {
    return permissions;
  }

  public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
    Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
        .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
        .collect(Collectors.toSet());
    permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return permissions;
  }
}
