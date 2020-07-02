package com.mmanchola.blog.service;

import static com.mmanchola.blog.exception.ExceptionMessage.INVALID;
import static com.mmanchola.blog.exception.ExceptionMessage.MISSING;
import static com.mmanchola.blog.exception.ExceptionMessage.MISSING_UNAVAILABLE;
import static com.mmanchola.blog.exception.ExceptionMessage.NOT_FOUND;
import static com.mmanchola.blog.model.TableFields.PERSON_EMAIL;
import static com.mmanchola.blog.model.TableFields.POST_PARENT_ID;
import static com.mmanchola.blog.model.TableFields.POST_SLUG;
import static com.mmanchola.blog.model.TableFields.POST_STATUS;
import static com.mmanchola.blog.model.TableFields.POST_TITLE;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.dao.PostDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Post;
import com.mmanchola.blog.model.PostStatus;
import com.mmanchola.blog.util.ServiceChecker;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PostService {
  private PostDataAccessService postDas;
  private PersonDataAccessService personDas;
  private ServiceChecker checker;

  @Autowired
  public PostService(PostDataAccessService postDas,
      PersonDataAccessService personDas, ServiceChecker checker) {
    this.postDas = postDas;
    this.personDas = personDas;
    this.checker = checker;
  }

  // Check given slug in terms of correctness and availability (Not-null attribute)
  private Optional<String> checkSlug(String slug) {
    return Optional.ofNullable(slug)
        .filter(Predicate.not(String::isEmpty))
        .map(s -> StringUtils.replace(s, " ", "-"))  // Replace blank spaces for dashes
        .filter(postDas::isSlugTaken);   // Check availability
  }

  // Check given status
  private Optional<String> checkStatus(String status) {
    return Optional.ofNullable(status)
        .filter(Predicate.not(String::isEmpty))
        .map(String::toLowerCase)
        .filter(PostStatus::contains);    // Must be contained in PostStatus enum
  }

  // Check post elements which are not allowed to be null and
  // refactor them appropriately if necessary
  private void checkPost(Post post) {
    // Check title
    checker.checkNotEmpty(post.getTitle())
        .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(POST_TITLE.toString())));
    // Check slug
    String slug = checkSlug(post.getSlug())
        .orElseThrow(() -> new ApiRequestException(MISSING_UNAVAILABLE.getMsg(POST_SLUG.toString())));
    post.setSlug(slug);
    // Check status
    String status = checkStatus(post.getStatus())
        .orElseThrow(() -> new ApiRequestException(INVALID.getMsg(POST_STATUS.toString())));
    post.setStatus(status);
  }

  // Add new parent post to database
  public int add(Post post, String authorEmail) {
    UUID authorId = personDas.findIdByEmail(authorEmail)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    post.setPersonId(authorId);
    checkPost(post);
    return postDas.saveParent(post);
  }

  // Add new child post to database
  public int add(Post post, String authorEmail, String parentSlug) {
    int parentId = postDas.findIdBySlug(parentSlug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_PARENT_ID.toString())));
    post.setParentId(parentId);
    return add(post, authorEmail);
  }

  // Get all posts
  public List<Post> getAll() { return postDas.findAll(); }

  // Get post(s) by its/their status
  public List<Post> getByStatus(String status) {
    String checkedStatus = checkStatus(status)
        .orElseThrow(() -> new ApiRequestException(INVALID.getMsg(POST_STATUS.toString())));
    return postDas.findByStatus(checkedStatus);
  }

  // Get post by its slug
  public Optional<Post> getBySlug(String slug) {
    String checkedSlug = checkSlug(slug)
        .orElseThrow(() -> new ApiRequestException(MISSING_UNAVAILABLE.getMsg(POST_SLUG.toString())));
    return postDas.findBySlug(checkedSlug);
  }

  // Get most recent post
  public List<Post> getMostRecent() {
    return getMostRecent(1);
  }

  // Get given number of most recent posts
  public List<Post> getMostRecent(int numPosts) {
    return postDas.findMostRecent(numPosts);
  }

  // Update author ID
  public void updateAuthor(String postSlug, String authorEmail) {
    UUID authorId = personDas.findIdByEmail(authorEmail)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    int postId = postDas.findIdBySlug(postSlug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
    postDas.updatePersonId(postId, authorId);
  }

  // Update post parent
  public void updateParent(String childSlug, String parentSlug) {
    int childId = postDas.findIdBySlug(childSlug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
    int parentId = postDas.findIdBySlug(parentSlug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
    postDas.updateParentId(childId, parentId);
  }

  // Update publication timestamp
  public void updatePublicationTime(String slug) {
    int postId = postDas.findIdBySlug(slug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
    postDas.updatePublishedAt(
        postId,
        new Timestamp(System.currentTimeMillis())
    );
  }

  // Update post-related fields
  public void updatePost(String slug, Post post) {
    // Retrieve ID from database
    int postId = postDas.findIdBySlug(slug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
    // Update title
    checker.checkNotEmpty(post.getTitle())
        .ifPresent(title -> postDas.updateTitle(postId, title));
    // Update metatitle
    checker.checkNotEmpty(post.getMetatitle())
        .ifPresent(metatitle -> postDas.updateMetatitle(postId, metatitle));
    // Update slug
    checkSlug(post.getSlug())
        .ifPresent(sl -> postDas.updateSlug(postId, sl));
    // Update status
    checkStatus(post.getStatus())
        .ifPresent(status -> postDas.updateStatus(postId, status));
    // Update update timestamp
    postDas.updateUpdatedAt(postId, new Timestamp(System.currentTimeMillis()));
    // Update content
    checker.checkNotEmpty(post.getContent())
        .ifPresent(content -> postDas.updateContent(postId, content));
  }

  // Delete post
  public int delete(String slug) {
    int postId = postDas.findIdBySlug(slug)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
    return postDas.delete(postId);
  }
}
