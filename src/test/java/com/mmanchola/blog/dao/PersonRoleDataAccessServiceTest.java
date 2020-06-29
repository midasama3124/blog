package com.mmanchola.blog.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonRoleDataAccessServiceTest {
  @Autowired
  private PersonRoleDataAccessService personRoleDas;
  @Autowired
  private PersonDataAccessService personDas;
  @Autowired
  private RoleDataAccessService roleDas;

  @Test
  public void canPerformCrudWhenValidIds() {
    UUID personId = personDas.findIdByEmail("test@gmail.com");
    short adminId = roleDas.findIdByName("ADMIN");
    short readerId = roleDas.findIdByName("READER");

    // Create
    int rowsAffected = personRoleDas.save(personId, readerId);
    assertEquals(1, rowsAffected);
    rowsAffected = personRoleDas.save(personId, adminId);
    assertEquals(1, rowsAffected);

    // Read
    List<Short> roleIds = personRoleDas.find(personId);
    assert roleIds.contains(adminId);
    assert roleIds.contains(readerId);

    // Delete
    rowsAffected = personRoleDas.delete(personId, readerId);
    assertEquals(1, rowsAffected);
    rowsAffected = personRoleDas.delete(personId, adminId);
    assertEquals(1, rowsAffected);
  }

}