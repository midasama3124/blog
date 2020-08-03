package com.mmanchola.blog.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Comment {

  private long id;
  private int postId;
  private UUID personId;
  private Timestamp publishedAt;
  private String content;

  public Comment(long id, int postId, UUID personId, Timestamp publishedAt, String content) {
    this.id = id;
    this.postId = postId;
    this.personId = personId;
    this.publishedAt = publishedAt;
    this.content = content;
  }

  public Comment() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getPostId() {
    return postId;
  }

  public void setPostId(int postId) {
    this.postId = postId;
  }

  public UUID getPersonId() {
    return personId;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
  }

  public Timestamp getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Timestamp publishedAt) {
    this.publishedAt = publishedAt;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
