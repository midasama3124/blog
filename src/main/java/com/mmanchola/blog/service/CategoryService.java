package com.mmanchola.blog.service;

import static com.mmanchola.blog.exception.ExceptionMessage.MISSING;
import static com.mmanchola.blog.exception.ExceptionMessage.NOT_FOUND;
import static com.mmanchola.blog.exception.ExceptionMessage.UNAVAILABLE;
import static com.mmanchola.blog.model.TableFields.CATEGORY_PARENT_ID;
import static com.mmanchola.blog.model.TableFields.CATEGORY_SLUG;
import static com.mmanchola.blog.model.TableFields.CATEGORY_TITLE;

import com.mmanchola.blog.dao.CategoryDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Category;
import com.mmanchola.blog.util.ServiceChecker;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  private CategoryDataAccessService categoryDas;
  private ServiceChecker checker;

  @Autowired
  public CategoryService(CategoryDataAccessService categoryDas,
      ServiceChecker checker) {
    this.categoryDas = categoryDas;
    this.checker = checker;
  }

  // Check category elements which are not allowed to be null and
  // refactor them appropriately if necessary
  private void checkCategory(Category category) {
    // Check title
    checker.checkNotEmpty(category.getTitle())
        .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(CATEGORY_TITLE.toString())));
    // Check slug
    String slug = checker.checkSlugCorrectness(category.getSlug())
        .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(CATEGORY_SLUG.toString())));
    checker.checkSlugAvailability(slug, categoryDas::isSlugTaken)
        .ifPresentOrElse(
            category::setSlug,
            () -> {
              throw new ApiRequestException(UNAVAILABLE.getMsg(CATEGORY_SLUG.toString()));
            }
        );
  }

  // Add new parent category to database
  public int add(Category category) {
    checkCategory(category);
    return categoryDas.saveParent(category);
  }

  // Add new child category to database
  public int add(Category category, String parentSlug) {
    int parentId = categoryDas.findIdBySlug(parentSlug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_PARENT_ID.toString())));
    category.setParentId(parentId);
    checkCategory(category);
    return categoryDas.saveChild(category);
  }

  // Get all categories
  public List<Category> getAll() { return categoryDas.findAll(); }

  // Get category by its slug
  public Optional<Category> getBySlug(String slug) {
    String checkedSlug = checker.checkSlugCorrectness(slug)
        .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(CATEGORY_SLUG.toString())));
    return categoryDas.findBySlug(checkedSlug);
  }

  // Update category parent
  public void updateParent(String childSlug, String parentSlug) {
    int childId = categoryDas.findIdBySlug(childSlug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
    int parentId = categoryDas.findIdBySlug(parentSlug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
    categoryDas.updateParentId(childId, parentId);
  }

  // Update category-related fields
  public void updateCategory(String slug, Category category) {
    // Retrieve ID from database
    int categoryId = categoryDas.findIdBySlug(slug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
    // Update title
    checker.checkNotEmpty(category.getTitle())
        .ifPresent(title -> categoryDas.updateTitle(categoryId, title));
    // Update metatitle
    checker.checkNotEmpty(category.getMetatitle())
        .ifPresent(metatitle -> categoryDas.updateMetatitle(categoryId, metatitle));
    // Update slug
    checker.checkSlugCorrectness(category.getSlug())
        .ifPresent(
            sl -> checker.checkSlugAvailability(sl, categoryId, categoryDas::isSlugTakenByOther)   // Is it taken by other?
                .ifPresentOrElse(
                    s -> categoryDas.updateSlug(categoryId, s),
                    () -> { throw new ApiRequestException(UNAVAILABLE.getMsg(CATEGORY_SLUG.toString())); }
                )
        );
    // Update content
    checker.checkNotEmpty(category.getContent())
        .ifPresent(content -> categoryDas.updateContent(categoryId, content));
  }

  // Delete category
  public int delete(String slug) {
    int categoryId = categoryDas.findIdBySlug(slug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
    return categoryDas.delete(categoryId);
  }
}
