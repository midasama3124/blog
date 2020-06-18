package com.mmanchola.blog.model;

import java.util.UUID;

public class PersonRole {

  private final UUID personId;
  private final short roleId;

  public PersonRole(UUID personId, short roleId) {
    this.personId = personId;
    this.roleId = roleId;
  }

  public UUID getPersonId() {
    return personId;
  }

  public short getRoleId() {
    return roleId;
  }

}
