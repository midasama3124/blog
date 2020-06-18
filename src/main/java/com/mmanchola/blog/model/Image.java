package com.mmanchola.blog.model;

public class Image {

  private final int id;
  private final String filepath;
  private final String caption;

  public Image(int id, String filepath, String caption) {
    this.id = id;
    this.filepath = filepath;
    this.caption = caption;
  }

  public int getId() {
    return id;
  }

  public String getFilepath() {
    return filepath;
  }

  public String getCaption() {
    return caption;
  }

}
