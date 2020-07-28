package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.PopularPost;
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

  Optional<Integer> findIdBySlug(String slug);

  List<PopularPost> findPopular(int numPosts);

  Optional<String> findSlugById(int postId);

  boolean isSlugTaken(String slug);
  boolean isSlugTakenByOther(String slug, int id);

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

  int updateSocialNetwork1(int postId, String socialNetwork1);

  int updateSocialNetwork2(int postId, String socialNetwork2);

  int updateSocialNetwork3(int postId, String socialNetwork3);

  // Delete
  int delete(int postId);
}
