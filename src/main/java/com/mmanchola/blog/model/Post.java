package com.mmanchola.blog.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Post {
  private final int id;
  private final UUID personId;
  private final int parentId;
  private final String parentPath;
  private final String title;
  private final String metatitle;
  private final String slug;
  private final String status;
  private final Timestamp publishedAt;
  private final Timestamp updatedAt;
  private final String content;

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

  public int getId() {
    return id;
  }

  public UUID getPersonId() {
    return personId;
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

  public String getStatus() {
    return status;
  }

  public Timestamp getPublishedAt() {
    return publishedAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public String getContent() {
    return content;
  }

}
