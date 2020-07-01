package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Post;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostDao {
  // Create
  int saveParent(Post post);
  int saveChild(Post post);

  // Read
  List<Post> findAll();
  List<Post> findByStatus(String status);
  Optional<Post> findBySlug(String slug);
  List<Post> findMostRecent();
  List<Post> findMostRecent(int numPosts);
  int findIdBySlug(String slug);

  // Update
  int updatePersonId(int postId, UUID personId);
  int updateParentId(int postId, int parentId);
  int updateTitle(int postId, String title);
  int updateMetatitle(int postId, String metatitle);
  int updateSlug(int postId, String slug);
  int updateStatus(int postId, String status);
  int updatePublishedAt(int postId, Timestamp publishedAt);
  int updateUpdatedAt(int postId, Timestamp updatedAt);
  int updateContent(int postId, String content);

  // Delete
  int delete(int postId);
}
