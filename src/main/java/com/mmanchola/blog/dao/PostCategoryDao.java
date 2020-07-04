package com.mmanchola.blog.dao;

import java.util.List;

public interface PostCategoryDao {
  // Create
  int save(int postId, int categoryId);

  // Read
  List<Integer> find(int postId);

  // Update
  int delete(int postId, int categoryId);
}
