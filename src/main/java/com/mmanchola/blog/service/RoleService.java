package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.model.Role;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RoleService {
  private RoleDataAccessService dataAccessService;

  @Autowired
  public RoleService(RoleDataAccessService dataAccessService) {
    this.dataAccessService = dataAccessService;
  }

  public int add(Role role) {
    // Check name
    Optional.ofNullable(role.getName())
        .filter(name -> !StringUtils.isEmpty(name))
        .ifPresent(name -> {
          role.setName(name.toUpperCase());;
        });

    return dataAccessService.save(role);
  }

  public Optional<Role> get(String name) {
    return dataAccessService.findByName(name);
  }

  public void update(String name, Role role) {
    short id = dataAccessService.findIdByName(name);

    // Check name
    Optional.ofNullable(role.getName())
        .filter(n -> !StringUtils.isEmpty(n))
        .ifPresent(n -> {
          dataAccessService.updateName(id, n.toUpperCase());
        });

    // Check description
    Optional.ofNullable(role.getDescription())
        .ifPresent(desc -> dataAccessService.updateDescription(id, desc));
  }

  public int delete(String name) {
    return dataAccessService.deleteByName(name);
  }
}
