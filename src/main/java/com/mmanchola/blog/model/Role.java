package com.mmanchola.blog.model;

public class Role {

  private final short id;
  private final String name;
  private final String description;

  public Role(short id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public short getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
