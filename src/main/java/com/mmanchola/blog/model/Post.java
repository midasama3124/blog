package com.mmanchola.blog.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Post {
  private int id;
  private UUID personId;
  private int parentId;
  private String parentPath;
  private String title;
  private String metatitle;
  private String slug;
  private String status;
  private Timestamp publishedAt;
  private Timestamp updatedAt;
  private String content;

  public Post(int id, UUID personId, int parentId, String parentPath, String title,
      String metatitle, String slug, String status, Timestamp publishedAt,
      Timestamp updatedAt, String content) {
    this.id = id;
    this.personId = personId;
    this.parentId = parentId;
    this.parentPath = parentPath;
    this.title = title;
    this.metatitle = metatitle;
    this.slug = slug;
    this.status = status;
    this.publishedAt = publishedAt;
    this.updatedAt = updatedAt;
    this.content = content;
  }

  public Post() { }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public UUID getPersonId() {
    return personId;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Timestamp getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Timestamp publishedAt) {
    this.publishedAt = publishedAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
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
    if (!(obj instanceof Post)) return false;
    Post other = (Post) obj;
    return this.id == other.getId() &&
        this.personId.equals(other.getPersonId()) &&
        this.parentId == other.getParentId() &&
        this.title.equals(other.getTitle()) &&
        this.metatitle.equals(other.getMetatitle()) &&
        this.slug.equals(other.getSlug()) &&
        this.status.equals(other.getStatus()) &&
        this.content.equals(other.getContent());
  }
}
