package com.mmanchola.blog.model;

public class PostTag {

  private final int postId;
  private final int tagId;

  public PostTag(int postId, int tagId) {
    this.postId = postId;
    this.tagId = tagId;
  }

  public int getPostId() {
    return postId;
  }

  public int getTagId() {
    return tagId;
  }

}
