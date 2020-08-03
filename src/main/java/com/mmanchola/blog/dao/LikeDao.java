package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Like;

import java.util.UUID;

public interface LikeDao {
    // Create
    int save(Like like);

    // Read
    int findLikesByPost(int postId);

    boolean exists(int postId, UUID personId);

    // Delete
    int delete(long likeId);
}
