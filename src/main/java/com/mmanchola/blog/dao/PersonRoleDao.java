package com.mmanchola.blog.dao;

import java.util.List;
import java.util.UUID;

public interface PersonRoleDao {
  // Create
  int save(UUID personId, short roleId);

  // Read
  List<Short> find(UUID personId);

  // Delete
  int delete(UUID personId, short roleId);
}
