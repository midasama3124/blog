package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.CategoryDataAccessService;
import com.mmanchola.blog.dao.PostCategoryDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Category;
import com.mmanchola.blog.util.FieldChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mmanchola.blog.exception.ExceptionMessage.*;
import static com.mmanchola.blog.model.TableFields.CATEGORY_SLUG;
import static com.mmanchola.blog.model.TableFields.CATEGORY_TITLE;

@Service
public class CategoryService {

    private CategoryDataAccessService categoryDas;
    private PostCategoryDataAccessService postCategoryDas;
    private FieldChecker checker;

    @Autowired
    public CategoryService(CategoryDataAccessService categoryDas,
                           PostCategoryDataAccessService postCategoryDas, FieldChecker checker) {
        this.categoryDas = categoryDas;
        this.postCategoryDas = postCategoryDas;
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

    // Add new category to database
    public int add(Category category) {
        checkCategory(category);
        if (category.getParentId() != 0) {
            return categoryDas.saveChild(category);
        } else {
            return categoryDas.saveParent(category);
        }
    }

    // Get all categories
    public List<Category> getAll() {
        return categoryDas.findAll();
    }

    // Get parent categories
    public List<Category> getParents() {
        return categoryDas.findParents();
    }

    // Get category by its slug
    public Category getBySlug(String slug) {
        String checkedSlug = checker.checkSlugCorrectness(slug)
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(CATEGORY_SLUG.toString())));
        Category category = categoryDas.findBySlug(checkedSlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
        return category;
    }

    // Get children
    public List<Category> getChildren(String slug) {
        int parentId = categoryDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
        return categoryDas.findChildren(parentId);
    }

    // Update category-related fields
    public void update(String slug, Category category) {
        // Retrieve ID from database
        int categoryId = categoryDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
        // Update title
        checker.checkNotEmpty(category.getTitle())
                .ifPresent(title -> categoryDas.updateTitle(categoryId, title));
        // Update metatitle
        categoryDas.updateMetatitle(categoryId, category.getMetatitle());
        // Update slug
        checker.checkSlugCorrectness(category.getSlug())
                .ifPresent(
                        sl -> checker.checkSlugAvailability(sl, categoryId, categoryDas::isSlugTakenByOther)   // Is it taken by other?
                                .ifPresentOrElse(
                                        s -> categoryDas.updateSlug(categoryId, s),
                                        () -> {
                                            throw new ApiRequestException(UNAVAILABLE.getMsg(CATEGORY_SLUG.toString()));
                                        }
                                )
                );
        // Update content
        checker.checkNotEmpty(category.getContent())
                .ifPresent(content -> categoryDas.updateContent(categoryId, content));
        // Update parent category
        int parentId = category.getParentId();
        if (parentId != categoryId)
            // 'None' option to remove parent category
            if (parentId == 0) categoryDas.updateParentIdToNull(categoryId);
            else categoryDas.updateParentId(categoryId, parentId);
    }

    // Delete category
    public int delete(String slug) {
        int categoryId = categoryDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
        return categoryDas.delete(categoryId);
    }

    // Remove all posts associations
    public int removePosts(String slug) {
        int categoryId = categoryDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
        return postCategoryDas.deleteByCategory(categoryId);
    }
}
