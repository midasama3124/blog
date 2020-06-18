package com.mmanchola.blog.model;

public class Tag {

  private final int id;
  private final String title;
  private final String metatitle;
  private final String slug;
  private final String content;

  public Tag(int id, String title, String metatitle, String slug, String content) {
    this.id = id;
    this.title = title;
    this.metatitle = metatitle;
    this.slug = slug;
    this.content = content;
  }

  public int getId() {
    return id;
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
