package com.mmanchola.blog.model;

public class Role {

  private short id;
  private String name;
  private String description;

  public Role(short id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public Role() { }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Role)) return false;
    Role other = (Role) obj;
    return this.name.equals(other.getName()) &&
        this.description.equals(other.getDescription());
  }
}
