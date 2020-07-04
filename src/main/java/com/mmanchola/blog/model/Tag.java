package com.mmanchola.blog.model;

public class Tag {

  private int id;
  private String title;
  private String metatitle;
  private String slug;
  private String content;

  public Tag(int id, String title, String metatitle, String slug, String content) {
    this.id = id;
    this.title = title;
    this.metatitle = metatitle;
    this.slug = slug;
    this.content = content;
  }

  public Tag() { }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
    if (!(obj instanceof Tag)) return false;
    Tag other = (Tag) obj;
    return this.title.equals(other.getTitle()) &&
        this.metatitle.equals(other.getMetatitle()) &&
        this.slug.equals(other.getSlug()) &&
        this.content.equals(other.getContent());
  }
}
