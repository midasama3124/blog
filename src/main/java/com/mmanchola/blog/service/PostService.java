package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.dao.PostDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Post;
import com.mmanchola.blog.model.PostStatus;
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

  @Autowired
  public PostService(PostDataAccessService postDas,
      PersonDataAccessService personDas) {
    this.postDas = postDas;
    this.personDas = personDas;
  }

  // Add new parent post to database
  public int addParent(Post post, String username) {
    UUID author = personDas.findIdByEmail(username);

    // Check title
    String title = post.getTitle();
    Optional.ofNullable(title)
        .filter(Predicate.not(String::isEmpty))
        .orElseThrow(() -> new ApiRequestException("Post title is missing"));

    // Check slug
    String slug = post.getSlug();
    Optional.ofNullable(slug)
        .filter(Predicate.not(String::isEmpty))
        .map(s -> StringUtils.replace(s, " ", "-"))  // Replace blank spaces for dashes
        .ifPresentOrElse(
            post::setSlug,
            () -> { throw new ApiRequestException("Post slug is missing"); }
        );

    // Check status
    String status = post.getStatus();
    Optional.ofNullable(status)
        .filter(Predicate.not(String::isEmpty))
        .map(String::toLowerCase)
        .filter(PostStatus::contains)
        .ifPresentOrElse(
            post::setSlug,
            () -> { throw new ApiRequestException("Invalid post status"); }
        );

    return postDas.saveParent(post);
  }

  // Add new child post to database
  public int addChild(Post post) {
    return 0;
  }
}
