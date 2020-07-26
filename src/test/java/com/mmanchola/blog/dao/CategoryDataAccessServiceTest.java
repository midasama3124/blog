package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryDataAccessServiceTest {

  @Autowired
  private CategoryDataAccessService categoryDas;

  @Test
  public void canPerformCRUDWhenValidCategory() {
    String title = "Category title";
    String metatitle = "Category metatitle";
    String slug = "category-slug";
    String content = "This is the content of a category";
    Category category = new Category();
    category.setTitle(title);
    category.setMetatitle(metatitle);
    category.setSlug(slug);
    category.setContent(content);

    // Create
    // Test saving parent category
    int rowsAffected = categoryDas.saveParent(category);
    assertEquals(1, rowsAffected);
    // Test finding tag ID
    int parentId = categoryDas.findIdBySlug(slug).orElse(null);
    // Test saving child category
    category.setSlug(slug + "1");
    category.setParentId(parentId);
    categoryDas.saveChild(category);

    // Read
    // Test finding tag by slug
    Category retrievedTag = categoryDas.find(slug + "1").orElse(null);
    assert retrievedTag.equals(category);
    // Adding more tags so methods with list output can be assessed
    int numCategories = 5;
    for (int i = 2; i <= numCategories; i++) {
      String newSlug = slug + i;
      category.setSlug(newSlug);
      categoryDas.saveParent(category);
    }
    // Test finding all
    List<Category> allTags = categoryDas.findAll();
    assertEquals(numCategories+1, allTags.size());
    // Test category availability method
    assert categoryDas.isSlugTaken(slug + numCategories);
    assertFalse(categoryDas.isSlugTaken(slug + String.format("%s", numCategories+1)));
    // Test category availability apart from self
    assertFalse(categoryDas.isSlugTakenByOther(slug, parentId));

    // Update
    int newParentId = categoryDas.findIdBySlug(slug + "2").orElse(null);
    String newTitle = "New category title";
    String newMetatitle = "New category metatitle";
    String newSlug = slug + "0";
    String newContent = "New category content";
    category.setParentId(newParentId);
    category.setTitle(newTitle);
    category.setMetatitle(newMetatitle);
    category.setSlug(newSlug);
    category.setContent(newContent);
    categoryDas.updateParentId(parentId, newParentId);
    categoryDas.updateTitle(parentId, newTitle);
    categoryDas.updateMetatitle(parentId, newMetatitle);
    categoryDas.updateSlug(parentId, newSlug);
    categoryDas.updateContent(parentId, newContent);
    retrievedTag = categoryDas.find(newSlug).orElse(null);
    assert retrievedTag.equals(category);

    // Delete
    int[] deleteOrder = {1,0,2,3,4,5};
    for (int i : deleteOrder) {
      String slugTmp = slug + i;
      int id = categoryDas.findIdBySlug(slugTmp).orElse(null);
      rowsAffected = categoryDas.delete(id);
      assertEquals(1, rowsAffected);
    }
  }
}