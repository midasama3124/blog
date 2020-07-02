package com.mmanchola.blog.service;

import static com.mmanchola.blog.exception.ExceptionMessage.MISSING_INVALID;
import static com.mmanchola.blog.model.TableFields.ROLE_NAME;

import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Role;
import com.mmanchola.blog.util.ServiceChecker;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
  private RoleDataAccessService dataAccessService;
  private ServiceChecker checker;

  @Autowired
  public RoleService(RoleDataAccessService dataAccessService,
      ServiceChecker checker) {
    this.dataAccessService = dataAccessService;
    this.checker = checker;
  }

  public int add(Role role) {
    // Check name
    checker.checkRoleName(role.getName())
        .ifPresentOrElse(
            role::setName,
            () -> { throw new ApiRequestException(MISSING_INVALID.getMsg(ROLE_NAME.toString())); }
        );

    return dataAccessService.save(role);
  }

  public Optional<Role> get(String name) {
    return dataAccessService.findByName(name);
  }

  public void update(String name, Role role) {
    short id = dataAccessService.findIdByName(name);

    // Check name
    checker.checkRoleName(role.getName())
        .ifPresent(r -> dataAccessService.updateName(id, r));

    // Check description
    checker.checkNotEmpty(role.getDescription())
        .ifPresent(desc -> dataAccessService.updateDescription(id, desc));
  }

  public int delete(String name) {
    return dataAccessService.deleteByName(name);
  }
}
