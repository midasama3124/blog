package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Role;
import com.mmanchola.blog.util.FieldChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.mmanchola.blog.exception.ExceptionMessage.MISSING_INVALID;
import static com.mmanchola.blog.exception.ExceptionMessage.NOT_FOUND;
import static com.mmanchola.blog.model.TableFields.ROLE_NAME;

@Service
public class RoleService {
  private RoleDataAccessService roleDas;
  private FieldChecker checker;

  @Autowired
  public RoleService(RoleDataAccessService roleDas,
                     FieldChecker checker) {
    this.roleDas = roleDas;
    this.checker = checker;
  }

  public int add(Role role) {
    // Check name
    checker.checkRoleName(role.getName())
        .ifPresentOrElse(
            role::setName,
            () -> { throw new ApiRequestException(MISSING_INVALID.getMsg(ROLE_NAME.toString())); }
        );
    return roleDas.save(role);
  }

  public Optional<Role> get(String name) {
    return roleDas.findByName(name);
  }

  public void update(String name, Role role) {
    short id = roleDas.findIdByName(name)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(ROLE_NAME.toString())));

    // Check name
    checker.checkRoleName(role.getName())
        .ifPresent(r -> roleDas.updateName(id, r));

    // Check description
    checker.checkNotEmpty(role.getDescription())
        .ifPresent(desc -> roleDas.updateDescription(id, desc));
  }

  public int delete(String name) {
    return roleDas.deleteByName(name);
  }
}
