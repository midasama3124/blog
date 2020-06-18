package com.mmanchola.blog.model;

public class PostCategory {

  private final int postId;
  private final int categoryId;

  public PostCategory(int postId, int categoryId) {
    this.postId = postId;
    this.categoryId = categoryId;
  }

  public int getPostId() {
    return postId;
  }

  public int getCategoryId() {
    return categoryId;
  }

}
