package com.mmanchola.blog.model;

public class PostImage {

  private final int postId;
  private final int imageId;

  public PostImage(int postId, int imageId) {
    this.postId = postId;
    this.imageId = imageId;
  }

  public int getPostId() {
    return postId;
  }

  public int getImageId() {
    return imageId;
  }

}
