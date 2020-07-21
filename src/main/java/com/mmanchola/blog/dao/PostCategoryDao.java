package com.mmanchola.blog.dao;

import java.util.Optional;

public interface PostCategoryDao {
    // Create
    int save(int postId, int categoryId);

    // Read
    Optional<Integer> find(int postId);

    // Delete
    int deleteSingle(int postId, int categoryId);

    int deleteByPost(int postId);

    int deleteByCategory(int categoryId);
}
