package com.mmanchola.blog.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.mmanchola.blog.model.Tag;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TagDataAccessServiceTest {

  @Autowired
  private TagDataAccessService tagDas;

  @Test
  public void canPerformCrudWhenValidTag() {
    String title = "Tag title";
    String metatitle = "Tag metatitle";
    String slug = "tag-slug";
    String content = "This is the content of a tag";
    Tag tag = new Tag();
    tag.setTitle(title);
    tag.setMetatitle(metatitle);
    tag.setSlug(slug);
    tag.setContent(content);

    // Create
    int rowsAffected = tagDas.save(tag);
    assertEquals(1, rowsAffected);

    // Read
    // Test finding tag ID
    int tagId = tagDas.findIdBySlug(slug).orElse(null);
    // Test finding tag by slug
    Tag retrievedTag = tagDas.findBySlug(slug).orElse(null);
    assert retrievedTag.equals(tag);
    // Adding more tags so methods with list output can be assessed
    int numTags = 5;
    for (int i = 1; i <= numTags; i++) {
      String newSlug = slug + i;
      tag.setSlug(newSlug);
      tagDas.save(tag);
    }
    // Test finding all
    List<Tag> allTags = tagDas.findAll();
    assertEquals(numTags+1, allTags.size());
    // Test tag availability method
    assert tagDas.isSlugTaken(slug + numTags);
    assertFalse(tagDas.isSlugTaken(slug + String.format("%s", numTags+1)));
    // Test tag availability apart from self
    assertFalse(tagDas.isSlugTakenByOther(slug, tagId));

    // Update
    String newTitle = "New tag title";
    String newMetatitle = "New tag metatitle";
    String newSlug = slug + String.format("%s", numTags+1);
    String newContent = "New tag content";
    tag.setTitle(newTitle);
    tag.setMetatitle(newMetatitle);
    tag.setSlug(newSlug);
    tag.setContent(newContent);
    tagDas.updateTitle(tagId, newTitle);
    tagDas.updateMetatitle(tagId, newMetatitle);
    tagDas.updateSlug(tagId, newSlug);
    tagDas.updateContent(tagId, newContent);
    retrievedTag = tagDas.findBySlug(newSlug).orElse(null);
    assert retrievedTag.equals(tag);

    // Delete
    for (int i = 1; i <= numTags+1; i++) {
      String slugTmp = slug + i;
      int id = tagDas.findIdBySlug(slugTmp).orElse(null);
      rowsAffected = tagDas.delete(id);
      assertEquals(1, rowsAffected);
    }
  }

}