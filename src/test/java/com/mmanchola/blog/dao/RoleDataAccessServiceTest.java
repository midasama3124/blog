package com.mmanchola.blog.dao;

import static org.junit.Assert.assertEquals;

import com.mmanchola.blog.model.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleDataAccessServiceTest {
  @Autowired
  private RoleDataAccessService dataAccessService;

  @Test
  public void canPerformCrudWhenValidRole() {
    short id = Short.valueOf("5");
    String name = "ROLE";
    String description = "Role description";
    Role firstRole = new Role(
      id,
      name,
      description
    );

    // Create
    int affectedRows = dataAccessService.save(firstRole);
    assertEquals(1, affectedRows);

    // Read
    Role retrievedRole = dataAccessService.findByName(firstRole.getName()).orElse(null);
    assert retrievedRole.equals(firstRole);
    id = dataAccessService.findIdByName(name).orElse(null);
    assertEquals(retrievedRole.getId(), id);
    name = dataAccessService.findNameById(id).orElse(null);
    assertEquals(retrievedRole.getName(), name);

    // Update
    String newName = "MEMBER";
    dataAccessService.updateName(id, newName);
    String newDescription = "New role description";
    dataAccessService.updateDescription(id, newDescription);
    retrievedRole = dataAccessService.findByName(newName).orElse(null);
    assertEquals(newDescription, retrievedRole.getDescription());

    // Delete
    affectedRows = dataAccessService.deleteByName(retrievedRole.getName());
    assertEquals(1, affectedRows);
  }
}