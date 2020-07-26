package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.CategoryDataAccessService;
import com.mmanchola.blog.dao.PostCategoryDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Category;
import com.mmanchola.blog.util.ServiceChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.mmanchola.blog.exception.ExceptionMessage.*;
import static com.mmanchola.blog.model.TableFields.CATEGORY_SLUG;
import static com.mmanchola.blog.model.TableFields.CATEGORY_TITLE;

@Service
public class CategoryService {

    private CategoryDataAccessService categoryDas;
    private PostCategoryDataAccessService postCategoryDas;
    private ServiceChecker checker;

    @Autowired
    public CategoryService(CategoryDataAccessService categoryDas,
                           PostCategoryDataAccessService postCategoryDas, ServiceChecker checker) {
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

    // Get category by its slug
    public Optional<Category> getBySlug(String slug) {
        String checkedSlug = checker.checkSlugCorrectness(slug)
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(CATEGORY_SLUG.toString())));
        return categoryDas.find(checkedSlug);
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
        checker.checkNotEmpty(category.getMetatitle())
                .ifPresent(metatitle -> categoryDas.updateMetatitle(categoryId, metatitle));
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
        if (category.getParentId() != categoryId)
            categoryDas.updateParentId(categoryId, category.getParentId());
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
