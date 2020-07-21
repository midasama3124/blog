package com.mmanchola.blog.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostTagDataAccessServiceTest {

  @Autowired
  private PostTagDataAccessService postTagDas;
  @Autowired
  private PostDataAccessService postDas;
  @Autowired
  private TagDataAccessService tagDas;

  @Test
  public void canPerformCrudWhenValidPostTag() {
    String postSlug = "test-slug";
    String tagSlug1 = "test";
    String tagSlug2 = "test2";
    int postId = postDas.findIdBySlug(postSlug).orElse(null);
    int tagId1 = tagDas.findIdBySlug(tagSlug1).orElse(null);
    int tagId2 = tagDas.findIdBySlug(tagSlug2).orElse(null);

    // Create
    int rowsAffected = postTagDas.save(postId, tagId1);
    assertEquals(1, rowsAffected);
    rowsAffected = postTagDas.save(postId, tagId2);
    assertEquals(1, rowsAffected);

    // Read
    List<Integer> allTagIds = postTagDas.find(postId);
    assertEquals(2, allTagIds.size());

    // Delete
    rowsAffected = postTagDas.deleteSingle(
            postId,
            tagId1
    );
    assertEquals(1, rowsAffected);
    rowsAffected = postTagDas.deleteSingle(postId, tagId2);
    assertEquals(1, rowsAffected);
  }
}