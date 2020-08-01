package com.mmanchola.blog.dao;

import java.util.List;

public interface PostTagDao {
    // Create
    int save(int postId, int tagId);

    // Read
    List<Integer> findByPost(int postId);

    List<Integer> findByTag(int tagId);

    // Delete
    int deleteSingle(int postId, int tagId);

    int deleteByPost(int postId);

    int deleteByTag(int tagId);
}
