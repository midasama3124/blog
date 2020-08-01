package com.mmanchola.blog.service;

import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.model.Post;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTest {

  @Autowired
  private PostService postService;
  @Autowired
  private PersonService personService;

  @Test
  public void canPerformCrudWhenValidPost() {
    String authorEmail = "test@gmail.com";
    String title = "Post title";
    String metatitle = "Post metatitle";
    String slug = "post-slug";
    String status = "published";
    String content = "Post content";
    Post post = new Post();
    post.setTitle(title);
    post.setMetatitle(metatitle);
    post.setSlug(slug);
    post.setStatus(status);
    post.setContent(content);

    // Create
    // Test adding parent post
    int rowsAffected = postService.add(post, authorEmail);
    assertEquals(1, rowsAffected);

    // Read
    // Test getting post by slug
    Post postRetrieved = postService.getBySlug(slug);
    assert postRetrieved.equals(post);

    // Create
    // Testing adding child post
    String newSlug = slug + "1";
//    post.setSlug(newSlug);
//    postService.add(
//        post,
//        authorEmail,
//        slug
//    );
//    postRetrieved = postService.getBySlug(newSlug).orElse(null);
//    assert postRetrieved.equals(post);

    // Update
    // Test updating publication timestamp
    postService.updatePublicationTime(slug);
    postRetrieved = postService.getBySlug(slug);
    assertNotEquals(0, postRetrieved.getPublishedAt().compareTo(postRetrieved.getUpdatedAt()));

    // Save multiple posts to test finding methods with list output
    int numPosts = 5;
//    for (int i = 2; i <= numPosts; i++) {
//      String slugTmp = String.format("post-slug%s", i);
//      post.setSlug(slugTmp);
//      postService.add(post, authorEmail, slug);
//      postService.updatePublicationTime(slugTmp);
//    }

    // Read
    // Test finding most recent post (single result)
    List<Post> mostRecent = postService.getMostRecent();
    Assert.assertEquals(String.format("post-slug%s", numPosts), mostRecent.get(0).getSlug());
    // Test finding most recent posts (list output)
    int numRecents = 3;
    List<Post> mostRecents = postService.getMostRecent(numRecents);
    for (int i = numPosts; i > numPosts - numRecents; i--)
      Assert.assertEquals(String.format("post-slug%s", i), mostRecents.get(numPosts - i).getSlug());
    // Test finding all posts
    List<Post> allPosts = postService.getAll();
    Assert.assertEquals(6, allPosts.size());

    // Update
    // Test updating post status
    postService.updateStatus(slug, "draft");
    postRetrieved = postService.getBySlug(slug);
    assert postRetrieved.getStatus().equals("draft");

    // Read
    // Test finding posts by status
    List<Post> publishedPosts = postService.getByStatus(status);
    Assert.assertEquals(5, publishedPosts.size());

    // Update
    String updateSlug = slug + "1";
    // Test updating author
    String newAuthorEmail = "midasama3124@gmail.com";
    postService.updateAuthor(updateSlug, newAuthorEmail);
    // Test updating the rest of post fields
    newSlug = slug + "6";
    String newTitle = "New post title";
    String newMetatitle = "New post metatitle";
    String newContent = "New post content";
    post.setSlug(newSlug);
    post.setTitle(newTitle);
    post.setMetatitle(newMetatitle);
    post.setContent(newContent);
    postService.update(updateSlug, post);

    postRetrieved = postService.getBySlug(newSlug);
    Person author = personService.get(newAuthorEmail);
    Assert.assertEquals(author.getId(), postRetrieved.getPersonId());
    Assert.assertEquals(newTitle, postRetrieved.getTitle());
    Assert.assertEquals(newMetatitle, postRetrieved.getMetatitle());
    Assert.assertEquals(newSlug, postRetrieved.getSlug());
    Assert.assertEquals(newContent, postRetrieved.getContent());

    // Delete
    // Modify post id of parent post in order to delete it from subsequent cycle
    post.setSlug(slug + "1");
    postService.update(slug, post);
    for (int i = 6; i > 0; i--) {
      String slugTmp = slug + i;
      rowsAffected = postService.delete(slugTmp);
      Assert.assertEquals(1, rowsAffected);
    }
  }

}