package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
  // Create
  int saveParent(Category category);
  int saveChild(Category category);

  // Read
  List<Category> findAll();
  Optional<Category> findBySlug(String slug);
  Optional<Integer> findIdBySlug(String slug);
  boolean isSlugTaken(String slug);
  boolean isSlugTakenByOther(String newSlug, int categoryId);

  // Update
  int updateParentId(int categoryId, int parentId);
  int updateTitle(int categoryId, String title);
  int updateMetatitle(int categoryId, String metatitle);
  int updateSlug(int categoryId, String slug);
  int updateContent(int categoryId, String content);

  // Delete
  int delete(int categoryId);
}
