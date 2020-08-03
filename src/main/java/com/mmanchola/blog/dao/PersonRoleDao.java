package com.mmanchola.blog.dao;

import java.util.List;
import java.util.UUID;

public interface PersonRoleDao {
    // Create
    int save(UUID personId, short roleId);

    // Read
    List<Short> findRoles(UUID personId);

    List<UUID> findPeople(short roleId);

    boolean hasRole(UUID personId, short roleId);

    // Delete
    int deleteSingleEntry(UUID personId, short roleId);

    int deletePerson(UUID personId);
}
