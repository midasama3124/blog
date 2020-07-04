package com.mmanchola.blog.dao;

import java.util.List;

public interface PostTagDao {
  // Create
  int save(int postId, int tagId);

  // Read
  List<Integer> find(int postId);

  // Delete
  int delete(int postId, int tagId);
}
