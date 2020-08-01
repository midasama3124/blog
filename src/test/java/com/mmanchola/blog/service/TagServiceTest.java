package com.mmanchola.blog.service;

import com.mmanchola.blog.model.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Test
    public void canPerformCRUDWhenValidTag() {
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
    int rowsAffected = tagService.add(tag);
    assertEquals(1, rowsAffected);

    // Read
    // Test finding tag by slug
        Tag retrievedTag = tagService.getBySlug(slug);
    assert retrievedTag.equals(tag);
    // Adding more tags so methods with list output can be assessed
    int numTags = 5;
    for (int i = 1; i <= numTags; i++) {
      String newSlug = slug + i;
      tag.setSlug(newSlug);
      tagService.add(tag);
    }
    // Test finding all
    List<Tag> allTags = tagService.getAll();
    assertEquals(numTags+1, allTags.size());

    // Update
    String newTitle = "New tag title";
    String newMetatitle = "New tag metatitle";
    String newSlug = slug + String.format("%s", numTags+1);
    String newContent = "New tag content";
    tag.setTitle(newTitle);
    tag.setMetatitle(newMetatitle);
    tag.setSlug(newSlug);
    tag.setContent(newContent);
        tagService.update(slug, tag);
        retrievedTag = tagService.getBySlug(newSlug);
    assert retrievedTag.equals(tag);

    // Delete
    for (int i = 1; i <= numTags+1; i++) {
      String slugTmp = slug + i;
      rowsAffected = tagService.delete(slugTmp);
      assertEquals(1, rowsAffected);
    }
  }

}