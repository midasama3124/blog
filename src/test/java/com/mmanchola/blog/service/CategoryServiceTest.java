package com.mmanchola.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mmanchola.blog.model.Category;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceTest {

  @Autowired
  private CategoryService categoryService;

  @Test
  public void canPerformCrudWhenValidCategory() {
    String title = "Category title";
    String metatitle = "Category metatitle";
    String slug = "category-slug";
    String content = "Category content";
    Category category = new Category();
    category.setTitle(title);
    category.setMetatitle(metatitle);
    category.setSlug(slug);
    category.setContent(content);

    // Create
    // Test adding parent category
    int rowsAffected = categoryService.add(category);
    assertEquals(1, rowsAffected);

    // Read
    // Test getting category by slug
    Category categoryRetrieved = categoryService.getBySlug(slug).orElse(null);
    assert categoryRetrieved.equals(category);

    // Create
    // Testing adding child category
    String newSlug = slug + "1";
    category.setSlug(newSlug);
    categoryService.add(
        category,
        slug
    );
    categoryRetrieved = categoryService.getBySlug(newSlug).orElse(null);
    assert categoryRetrieved.equals(category);

    // Save multiple posts to test finding methods with list output
    int numPosts = 5;
    for (int i = 2; i <= numPosts; i++) {
      String slugTmp = slug + i;
      category.setSlug(slugTmp);
      categoryService.add(category, slug);
    }

    // Read
    // Test finding all posts
    List<Category> allPosts = categoryService.getAll();
    Assert.assertEquals(6, allPosts.size());

    // Update
    String updateSlug = slug + "1";
    // Test updating parent ID
    String parentSlug = slug + "2";
    categoryService.updateParent(updateSlug, parentSlug);
    // Test updating the rest of category fields
    newSlug = slug + "6";
    String newTitle = "New category title";
    String newMetatitle = "New category metatitle";
    String newContent = "New category content";
    category.setSlug(newSlug);
    category.setTitle(newTitle);
    category.setMetatitle(newMetatitle);
    category.setContent(newContent);
    categoryService.updateCategory(updateSlug, category);

    categoryRetrieved = categoryService.getBySlug(newSlug).orElse(null);
    Category parent = categoryService.getBySlug(parentSlug).orElse(null);
    Assert.assertEquals(parent.getId(), categoryRetrieved.getParentId());
    Assert.assertEquals(newTitle, categoryRetrieved.getTitle());
    Assert.assertEquals(newMetatitle, categoryRetrieved.getMetatitle());
    Assert.assertEquals(newSlug, categoryRetrieved.getSlug());
    Assert.assertEquals(newContent, categoryRetrieved.getContent());

    // Delete
    // Modify category id of parent category in order to delete it from subsequent cycle
    category.setSlug(slug + "1");
    categoryService.updateCategory(slug, category);
    for (int i = 6; i > 0; i--) {
      String slugTmp = slug + i;
      rowsAffected = categoryService.delete(slugTmp);
      Assert.assertEquals(1, rowsAffected);
    }
  }
}