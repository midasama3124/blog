package com.mmanchola.blog.dao;

import java.util.List;
import java.util.UUID;

public interface PersonRoleDao {
    // Create
    int save(UUID personId, short roleId);

    // Read
    List<Short> findRoles(UUID personId);

    List<UUID> findPeople(short roleId);

    // Delete
    int delete(UUID personId, short roleId);
}
