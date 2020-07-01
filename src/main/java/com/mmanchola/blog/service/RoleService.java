package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.model.Role;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        .filter(Predicate.not(String::isEmpty))
        .map(String::toUpperCase)
        .ifPresentOrElse(role::setName, IllegalStateException::new);

    return dataAccessService.save(role);
  }

  public Optional<Role> get(String name) {
    return dataAccessService.findByName(name);
  }

  public void update(String name, Role role) {
    short id = dataAccessService.findIdByName(name);

    // Check name
    Optional.ofNullable(role.getName())
        .filter(Predicate.not(String::isEmpty))
        .map(String::toUpperCase)
        .ifPresent(r -> dataAccessService.updateName(id, r));

    // Check description
    Optional.ofNullable(role.getDescription())
        .filter(Predicate.not(String::isEmpty))
        .ifPresent(desc -> dataAccessService.updateDescription(id, desc));
  }

  public int delete(String name) {
    return dataAccessService.deleteByName(name);
  }
}
