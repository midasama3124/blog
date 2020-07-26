package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentDao {
    // Create
    int save(Comment comment);

    // Read
    List<Comment> findAll();

    Optional<Comment> find(long commentId);

    Optional<Comment> find(int postId, UUID personId);

    List<Comment> findByPost(int postId);

    List<Comment> findByReaderAndPost(UUID personId, int postId);

    boolean exists(long commentId);

    // Delete
    int delete(long commentId);
}
