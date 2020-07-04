package com.mmanchola.blog.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostCategoryDataAccessServiceTest {

  @Autowired
  private PostCategoryDataAccessService postCategoryDas;
  @Autowired
  private PostDataAccessService postDas;
  @Autowired
  private CategoryDataAccessService categoryDas;

  @Test
  public void canPerformCrudWhenValidPostCategory() {
    String postSlug = "test-slug";
    String categorySlug1 = "test1";
    String categorySlug2 = "test2";
    int postId = postDas.findIdBySlug(postSlug).orElse(null);
    int categoryId1 = categoryDas.findIdBySlug(categorySlug1).orElse(null);
    int categoryId2 = categoryDas.findIdBySlug(categorySlug2).orElse(null);

    // Create
    int rowsAffected = postCategoryDas.save(postId, categoryId1);
    assertEquals(1, rowsAffected);
    rowsAffected = postCategoryDas.save(postId, categoryId2);
    assertEquals(1, rowsAffected);

    // Read
    List<Integer> allTagIds = postCategoryDas.find(postId);
    assertEquals(2, allTagIds.size());

    // Delete
    rowsAffected = postCategoryDas.delete(
        postId,
        categoryId1
    );
    assertEquals(1, rowsAffected);
    rowsAffected = postCategoryDas.delete(postId, categoryId2);
    assertEquals(1, rowsAffected);
  }

}