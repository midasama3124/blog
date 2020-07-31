package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.*;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Comment;
import com.mmanchola.blog.model.Like;
import com.mmanchola.blog.model.PopularPost;
import com.mmanchola.blog.model.Post;
import com.mmanchola.blog.util.FieldChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mmanchola.blog.exception.ExceptionMessage.*;
import static com.mmanchola.blog.model.TableFields.*;

@Service
public class PostService {
    private PostDataAccessService postDas;
    private PersonDataAccessService personDas;
    private PostTagDataAccessService postTagDas;
    private TagDataAccessService tagDas;
    private CategoryDataAccessService categoryDas;
    private PostCategoryDataAccessService postCategoryDas;
    private CommentDataAccessService commentDas;
    private LikeDataAccessService likeDas;
    private FieldChecker checker;

    @Autowired
    public PostService(PostDataAccessService postDas,
                       PersonDataAccessService personDas, PostTagDataAccessService postTagDas,
                       TagDataAccessService tagDas, CategoryDataAccessService categoryDas,
                       PostCategoryDataAccessService postCategoryDas, CommentDataAccessService commentDas, LikeDataAccessService likeDas, FieldChecker checker) {
        this.postDas = postDas;
        this.personDas = personDas;
        this.postTagDas = postTagDas;
        this.tagDas = tagDas;
        this.categoryDas = categoryDas;
        this.postCategoryDas = postCategoryDas;
        this.commentDas = commentDas;
        this.likeDas = likeDas;
        this.checker = checker;
    }

    // Check post elements which are not allowed to be null and
    // refactor them appropriately if necessary
    private void checkPost(Post post) {
        // Check title
        checker.checkNotEmpty(post.getTitle())
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(POST_TITLE.toString())));
        // Check slug
        String slug = checker.checkSlugCorrectness(post.getSlug())
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(POST_SLUG.toString())));
        checker.checkSlugAvailability(slug, postDas::isSlugTaken)
                .ifPresentOrElse(
                        post::setSlug,
                        () -> {
                            throw new ApiRequestException(UNAVAILABLE.getMsg(POST_SLUG.toString()));
                        }
                );
        // Check status
        String status = checker.checkStatus(post.getStatus())
                .orElseThrow(() -> new ApiRequestException(INVALID.getMsg(POST_STATUS.toString())));
        post.setStatus(status);
        // Check social network links
        checker.checkUrl(post.getSocialNetwork1())
                .orElseThrow(() -> new ApiRequestException(INVALID.getMsg(POST_SOCIAL_NETWORK1.toString())));
        checker.checkUrl(post.getSocialNetwork2())
                .orElseThrow(() -> new ApiRequestException(INVALID.getMsg(POST_SOCIAL_NETWORK2.toString())));
    }

    // Add new parent post to database
    public int add(Post post, String authorEmail) {
        UUID authorId = personDas.findIdByEmail(authorEmail)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        post.setPersonId(authorId);
        checkPost(post);
        if (post.getParentId() != 0) {
            return postDas.saveChild(post);
        } else {
            return postDas.saveParent(post);
        }
    }

    // Add tag to post given their slug and id, respectively
    public void addTags(String postSlug, List<Integer> tagIds) {
        int postId = postDas.findIdBySlug(postSlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        for (int tagId : tagIds)
            postTagDas.save(postId, tagId);
    }

    // Add category to post given their slugs
    public int addCategory(String postSlug, String categorySlug) {
        int postId = postDas.findIdBySlug(postSlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        int categoryId = categoryDas.findIdBySlug(categorySlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
        return postCategoryDas.save(postId, categoryId);
    }

    // Add category to post given their slug and id, respectively
    public int addCategory(String postSlug, int categoryId) {
        int postId = postDas.findIdBySlug(postSlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return postCategoryDas.save(postId, categoryId);
    }

    // Add comment to post
    public int addComment(Comment comment, int postId, String memberEmail) {
        UUID authorId = personDas.findIdByEmail(memberEmail)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        // Check number of comments posted
        int commentThresh = 2;      // Only 2 comments are allowed per user on every post
        int numComments = commentDas.findByReaderAndPost(authorId, postId).size();
        if (numComments >= commentThresh) {
            throw new ApiRequestException(EXCEEDED.getMsg(COMMENT_THRESHOLD.toString()));
        }
        // Check content
        String content = comment.getContent();
        if (content.length() > 500)
            throw new ApiRequestException(EXCEEDED.getMsg(COMMENT_CONTENT.toString()));
        // Set post and author IDs
        comment.setPostId(postId);
        comment.setPersonId(authorId);
        return commentDas.save(comment);
    }

    // Add like to post
    public int addLike(Like like, int postId, String memberEmail) {
        like.setPostId(postId);
        UUID authorId = personDas.findIdByEmail(memberEmail)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        like.setPersonId(authorId);
        if (!likeDas.exists(postId, authorId)) return likeDas.save(like);
        return 0;
    }

    // Get all posts
    public List<Post> getAll() {
        return postDas.findAll();
    }

    // Get all tags
    public List<Integer> getTags(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return postTagDas.find(postId);
    }

    // Get corresponding category
    public Optional<Integer> getCategory(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return postCategoryDas.findByPost(postId);
    }

    // Get all comments corresponding to given post
    public List<Comment> getComments(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return commentDas.findByPost(postId);
    }

    // Get number of likes of given post
    public int getLikes(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return likeDas.findLikesByPost(postId);
    }

    // Get popular posts (i.e., based on number likes received)
    public List<PopularPost> getPopular(int numPosts) {
        return postDas.findPopular(numPosts);
    }

    // Get posts by category slug
    public List<Post> getByCategory(String categorySlug) {
        int categoryId = categoryDas.findIdBySlug(categorySlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(CATEGORY_SLUG.toString())));
        List<Post> posts = postCategoryDas.findByCategory(categoryId)
                .stream().map(id -> postDas.find(id).get())
                .collect(Collectors.toList());
        return posts;
    }

    // Has the corresponding post already been liked by given reader
    public boolean isAlreadyLiked(String postSlug, String memberEmail) {
        int postId = postDas.findIdBySlug(postSlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        UUID personId = personDas.findIdByEmail(memberEmail)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        return likeDas.exists(postId, personId);
    }

    // Get post(s) by its/their status
    public List<Post> getByStatus(String status) {
        String checkedStatus = checker.checkStatus(status)
                .orElseThrow(() -> new ApiRequestException(INVALID.getMsg(POST_STATUS.toString())));
        return postDas.findByStatus(checkedStatus);
    }

    // Get post by its slug
    public Post getBySlug(String slug) {
        String checkedSlug = checker.checkSlugCorrectness(slug)
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(POST_SLUG.toString())));
        return postDas.findBySlug(checkedSlug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
    }

    // Get post slug by its ID
    public String getSlugById(int postId) {
        return postDas.findSlugById(postId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_ID.toString())));
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

    // Update publication timestamp
    public void updatePublicationTime(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        postDas.updatePublishedAt(
                postId,
                new Timestamp(System.currentTimeMillis())
        );
    }

    // Update post status
    public void updateStatus(String slug, String status) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(MISSING_INVALID.getMsg(POST_SLUG.toString())));
        // Update status
        checker.checkStatus(status)
                .ifPresentOrElse(
                        st -> postDas.updateStatus(postId, st),
                        () -> {
                            throw new ApiRequestException(MISSING_INVALID.getMsg(POST_STATUS.toString()));
                        }
                );
    }

    // Update post-related fields
    public void update(String slug, Post post) {
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
        checker.checkSlugCorrectness(post.getSlug())
                .ifPresent(
                        sl -> checker.checkSlugAvailability(sl, postId, postDas::isSlugTakenByOther)   // Is it taken by other?
                                .ifPresentOrElse(
                                        s -> postDas.updateSlug(postId, s),
                                        () -> {
                                            throw new ApiRequestException(UNAVAILABLE.getMsg(POST_SLUG.toString()));
                                        }
                                )
                );
        // Update update timestamp
        postDas.updateUpdatedAt(postId, new Timestamp(System.currentTimeMillis()));
        // Update content
        checker.checkNotEmpty(post.getContent())
                .ifPresent(content -> postDas.updateContent(postId, content));
        // Update parent
        if (post.getParentId() != 0) {
            postDas.updateParentId(postId, post.getParentId());
        }
        // Update social network links
        checker.checkUrl(post.getSocialNetwork1())
                .ifPresent(socialNetwork -> postDas.updateSocialNetwork1(postId, socialNetwork));
        checker.checkUrl(post.getSocialNetwork2())
                .ifPresent(socialNetwork -> postDas.updateSocialNetwork2(postId, socialNetwork));
        checker.checkUrl(post.getSocialNetwork3())
                .ifPresent(socialNetwork -> postDas.updateSocialNetwork3(postId, socialNetwork));
    }

    // Delete post
    public int delete(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return postDas.delete(postId);
    }

    // Remove all tags associated
    public int removeTags(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return postTagDas.deleteByPost(postId);
    }

    // Remove all categories associated
    public int removeCategory(String slug) {
        int postId = postDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(POST_SLUG.toString())));
        return postCategoryDas.deleteByPost(postId);
    }
}
