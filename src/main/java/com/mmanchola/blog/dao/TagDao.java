package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.PopularTag;
import com.mmanchola.blog.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
  // Create
  int save(Tag tag);

  // Read
  List<Tag> findAll();

  Optional<Tag> find(int id);

  Optional<Tag> findBySlug(String slug);

  Optional<Integer> findIdBySlug(String slug);

  List<PopularTag> findPopular(int numTags);

  boolean isSlugTaken(String slug);
  boolean isSlugTakenByOther(String newSlug, int tagId);

  // Update
  int updateTitle(int tagId, String title);
  int updateMetatitle(int tagId, String metatitle);
  int updateSlug(int tagId, String slug);
  int updateContent(int tagId, String content);

  // Delete
  int delete(int tagId);

}
