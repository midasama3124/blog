package com.mmanchola.blog.service;

import static org.junit.Assert.assertEquals;

import com.mmanchola.blog.model.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleServiceTest {
  @Autowired
  RoleService roleService;

  @Test
  public void canPerformCrudWhenValidRole() {
    String name = "ROLE";
    String description = "Role description";
    Role role = new Role(
        Short.valueOf("0"),
        name,
        description
    );

    // Create
    int rowsAffected = roleService.add(role);
    assertEquals(1, rowsAffected);

    // Read
    Role roleRetrieved = roleService.get(name).orElse(null);
    assert roleRetrieved.equals(role);

    // Update
    String newName = "MEMBER";
    role.setName(newName);
    role.setDescription("New role description");
    roleService.update(name, role);
    roleRetrieved = roleService.get(newName).orElse(null);
    assert roleRetrieved.equals(role);

    // Delete
    rowsAffected = roleService.delete(newName);
    assertEquals(1, rowsAffected);
  }
}