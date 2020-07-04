package com.mmanchola.blog.model;

public class Category {

  private int id;
  private int parentId;
  private String parentPath;
  private String title;
  private String metatitle;
  private String slug;
  private String content;

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

  public Category() { }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getParentId() {
    return parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public String getParentPath() {
    return parentPath;
  }

  public void setParentPath(String parentPath) {
    this.parentPath = parentPath;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMetatitle() {
    return metatitle;
  }

  public void setMetatitle(String metatitle) {
    this.metatitle = metatitle;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Category)) return false;
    Category other = (Category) obj;
    return this.parentId == other.getParentId() &&
        this.title.equals(other.getTitle()) &&
        this.metatitle.equals(other.getMetatitle()) &&
        this.slug.equals(other.getSlug()) &&
        this.content.equals(other.getContent());
  }
}
