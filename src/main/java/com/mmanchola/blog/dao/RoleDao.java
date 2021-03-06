package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Role;
import java.util.Optional;

public interface RoleDao {
  // Create
  int save(Role role);

  // Read
  Optional<Role> findByName(String name);
  Optional<String> findNameById(short id);
  Optional<Short> findIdByName(String name);

  // Update
  int updateName(short id, String name);
  int updateDescription(short id, String description);

  // Delete
  int deleteByName(String name);
}
