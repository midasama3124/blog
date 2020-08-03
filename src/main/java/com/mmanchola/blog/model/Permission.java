package com.mmanchola.blog.model;

public class Permission {

  private final int id;
  private final String name;

  public Permission(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

}
