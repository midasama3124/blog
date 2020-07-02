package com.mmanchola.blog.dao;

import static org.junit.Assert.assertEquals;

import com.mmanchola.blog.model.Post;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostDataAccessServiceTest {
  @Autowired
  private PostDataAccessService posdtDas;
  @Autowired
  private PersonDataAccessService personDas;

  @Test
  public void canPerformCrudWhenValidPost() {
    UUID personId = personDas.findIdByEmail("midasama3124@gmail.com").orElse(null);
    String title = "Post title";
    String metatitle = "Post metatitle";
    String slug = "post-slug";
    String status = "published";
    String content = "Post content";
    Post post = new Post();
    post.setPersonId(personId);
    post.setTitle(title);
    post.setMetatitle(metatitle);
    post.setSlug(slug);
    post.setStatus(status);
    post.setContent(content);

    // Create
    // Test saving a parent
    int rowsAffected = posdtDas.saveParent(post);
    assertEquals(1, rowsAffected);

    // Read
    // Test getting post ID by slug
    int id = posdtDas.findIdBySlug(slug).orElse(0);
    post.setId(id);
    Post postRetrieved = posdtDas.findBySlug(slug).orElse(null);
    assert postRetrieved.equals(post);

    // Create
    // Test saving a child
    post.setSlug("post-slug1");
    post.setParentId(id);
    rowsAffected = posdtDas.saveChild(post);
    assertEquals(1, rowsAffected);

    // Update
    // Test updating publication timestamp
    Timestamp curr = new Timestamp(System.currentTimeMillis());
    posdtDas.updatePublishedAt(id, curr);
    postRetrieved = posdtDas.findBySlug("post-slug").orElse(null);
    assert curr.equals(postRetrieved.getPublishedAt());

    // Save multiple posts to test finding methods with list output
    int numPosts = 5;
    for (int i = 2; i <= numPosts; i++) {
      String slugTmp = String.format("post-slug%s", i);
      post.setSlug(slugTmp);
      posdtDas.saveChild(post);
      int idTmp = posdtDas.findIdBySlug(slugTmp).orElse(0);
      posdtDas.updatePublishedAt(idTmp, new Timestamp(System.currentTimeMillis()));
    }

    // Read
    // Test finding most recent post (single result)
    List<Post> mostRecent = posdtDas.findMostRecent();
    assertEquals(String.format("post-slug%s", numPosts), mostRecent.get(0).getSlug());
    // Test finding most recent posts (list output)
    int numRecents = 3;
    List<Post> mostRecents = posdtDas.findMostRecent(numRecents);
    for (int i = numPosts; i > numPosts - numRecents; i--)
      assertEquals(String.format("post-slug%s", i), mostRecents.get(numPosts-i).getSlug());
    // Test finding all posts
    List<Post> allPosts = posdtDas.findAll();
    assertEquals(6, allPosts.size());

    // Update
    // Test updating post status
    id = posdtDas.findIdBySlug(slug).orElse(0);
    posdtDas.updateStatus(id, "draft");
    postRetrieved = posdtDas.findBySlug(slug).orElse(null);
    assert postRetrieved.getStatus().equals("draft");

    // Read
    // Test finding posts by status
    List<Post> publishedPosts = posdtDas.findByStatus(status);
    assertEquals(5, publishedPosts.size());

    // Update
    id = posdtDas.findIdBySlug("post-slug1").orElse(0);
    // Test updating person ID
    personId = personDas.findIdByEmail("test@gmail.com").orElse(null);
    posdtDas.updatePersonId(id, personId);
    // Test updating parent ID
    int parentId = posdtDas.findIdBySlug("post-slug2").orElse(0);
    posdtDas.updateParentId(id, parentId);
    // Test updating title
    String newTitle = "New post title";
    posdtDas.updateTitle(id, newTitle);
    // Test updating metatitle
    String newMetatitle = "New post metatitle";
    posdtDas.updateMetatitle(id, newMetatitle);
    // Test updating slug
    String newSlug = "post-slug6";
    posdtDas.updateSlug(id, newSlug);
    // Test updating update timestamp
    Timestamp newUpdatedAt = new Timestamp(System.currentTimeMillis());
    posdtDas.updateUpdatedAt(id, newUpdatedAt);
    // Test updating content
    String newContent = "New post content";
    posdtDas.updateContent(id, newContent);

    postRetrieved = posdtDas.findBySlug(newSlug).orElse(null);
    assertEquals(personId, postRetrieved.getPersonId());
    assertEquals(parentId, postRetrieved.getParentId());
    assertEquals(newTitle, postRetrieved.getTitle());
    assertEquals(newMetatitle, postRetrieved.getMetatitle());
    assertEquals(newSlug, postRetrieved.getSlug());
    assertEquals(newUpdatedAt, postRetrieved.getUpdatedAt());
    assertEquals(newContent, postRetrieved.getContent());

    // Delete
    // Modify post id of parent post in order to delete it from subsequent cycle
    id = posdtDas.findIdBySlug(slug).orElse(0);
    posdtDas.updateSlug(id,"post-slug1");
    for (int i = 6; i > 0; i--) {
      id = posdtDas.findIdBySlug(String.format("post-slug%s", i)).orElse(0);
      rowsAffected = posdtDas.delete(id);
      assertEquals(1, rowsAffected);
    }
  }
}