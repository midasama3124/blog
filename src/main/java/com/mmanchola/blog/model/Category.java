package com.mmanchola.blog.model;

public class Category {

  private final int id;
  private final int parentId;
  private final String parentPath;
  private final String title;
  private final String metatitle;
  private final String slug;
  private final String content;

  public Category(int id, int parentId, String parentPath, String title, String metatitle,
      String slug, String content) {
    this.id = id;
    this.parentId = parentId;
    this.parentPath = parentPath;
    this.title = title;
    this.metatitle = metatitle;
    this.slug = slug;
    this.content = content;
  }

  public int getId() {
    return id;
  }

  public int getParentId() {
    return parentId;
  }

  public String getParentPath() {
    return parentPath;
  }

  public String getTitle() {
    return title;
  }

  public String getMetatitle() {
    return metatitle;
  }

  public String getSlug() {
    return slug;
  }

  public String getContent() {
    return content;
  }

}
