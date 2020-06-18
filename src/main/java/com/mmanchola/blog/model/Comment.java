package com.mmanchola.blog.model;

import java.sql.Timestamp;

public class Comment {

  private final long id;
  private final int postId;
  private final String status;
  private final Timestamp publishedAt;
  private final String content;

  public Comment(long id, int postId, String status, Timestamp publishedAt, String content) {
    this.id = id;
    this.postId = postId;
    this.status = status;
    this.publishedAt = publishedAt;
    this.content = content;
  }

  public long getId() {
    return id;
  }

  public int getPostId() {
    return postId;
  }

  public String getStatus() {
    return status;
  }

  public Timestamp getPublishedAt() {
    return publishedAt;
  }

  public String getContent() {
    return content;
  }

}
