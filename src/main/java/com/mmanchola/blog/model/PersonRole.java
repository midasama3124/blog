package com.mmanchola.blog.model;

import java.util.UUID;

public class PersonRole {

  private UUID personId;
  private short roleId;

  public PersonRole(UUID personId, short roleId) {
    this.personId = personId;
    this.roleId = roleId;
  }

  public PersonRole() { }

  public UUID getPersonId() {
    return personId;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
  }

  public short getRoleId() {
    return roleId;
  }

  public void setRoleId(short roleId) {
    this.roleId = roleId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof PersonRole)) return false;
    PersonRole other = (PersonRole) obj;
    return this.personId.equals(other.getPersonId()) &&
        this.roleId == other.getRoleId();
  }
}
