package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.PostTagDataAccessService;
import com.mmanchola.blog.dao.TagDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.PopularTag;
import com.mmanchola.blog.model.Tag;
import com.mmanchola.blog.util.ServiceChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.mmanchola.blog.exception.ExceptionMessage.*;
import static com.mmanchola.blog.model.TableFields.TAG_SLUG;
import static com.mmanchola.blog.model.TableFields.TAG_TITLE;

@Service
public class TagService {

    private TagDataAccessService tagDas;
    private PostTagDataAccessService postTagDas;
    private ServiceChecker checker;

    @Autowired
    public TagService(TagDataAccessService tagDas, PostTagDataAccessService postTagDas, ServiceChecker checker) {
        this.tagDas = tagDas;
        this.postTagDas = postTagDas;
        this.checker = checker;
    }

    // Check tag elements which are not allowed to be null and
    // refactor them appropriately if necessary
    private void checkTag(Tag tag) {
        // Check title
        checker.checkNotEmpty(tag.getTitle())
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(TAG_TITLE.toString())));
        // Check slug
        String slug = checker.checkSlugCorrectness(tag.getSlug())
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(TAG_SLUG.toString())));
        checker.checkSlugAvailability(slug, tagDas::isSlugTaken)
                .ifPresentOrElse(
                        tag::setSlug,
                        () -> {
                            throw new ApiRequestException(UNAVAILABLE.getMsg(TAG_SLUG.toString()));
                        }
                );
    }

    // Add new tag
    public int add(Tag tag) {
        checkTag(tag);
        return tagDas.save(tag);
    }

    // Get all tags
    public List<Tag> getAll() {
        return tagDas.findAll();
    }

    // Get popular tags
    public List<PopularTag> getPopular(int numTags) {
        return tagDas.findPopular(numTags);
    }

    // Get tag by its ID
    public Optional<Tag> get(int id) {
        return tagDas.find(id);
    }

    // Get tag by its slug
    public Optional<Tag> getBySlug(String slug) {
        String checkedSlug = checker.checkSlugCorrectness(slug)
                .orElseThrow(() -> new ApiRequestException(MISSING.getMsg(TAG_SLUG.toString())));
        return tagDas.findBySlug(checkedSlug);
    }

    // Update tag
    public void update(String slug, Tag tag) {
        // Retrieve ID from database
        int tagId = tagDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(TAG_SLUG.toString())));
        // Update title
        checker.checkNotEmpty(tag.getTitle())
                .ifPresent(title -> tagDas.updateTitle(tagId, title));
        // Update metatitle
        checker.checkNotEmpty(tag.getMetatitle())
                .ifPresent(metatitle -> tagDas.updateMetatitle(tagId, metatitle));
        // Update slug
        checker.checkSlugCorrectness(tag.getSlug())
                .ifPresent(
                        sl -> checker.checkSlugAvailability(sl, tagId, tagDas::isSlugTakenByOther)   // Is it taken by other?
                                .ifPresentOrElse(
                                        s -> tagDas.updateSlug(tagId, s),
                                        () -> {
                                            throw new ApiRequestException(UNAVAILABLE.getMsg(TAG_SLUG.toString()));
                                        }
                                )
                );
        // Update content
        checker.checkNotEmpty(tag.getContent())
                .ifPresent(content -> tagDas.updateContent(tagId, content));
    }

    // Delete tag
    public int delete(String slug) {
        int tagId = tagDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(TAG_SLUG.toString())));
        return tagDas.delete(tagId);
    }

    // Remove all posts associated
    public int removePosts(String slug) {
        int tagId = tagDas.findIdBySlug(slug)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(TAG_SLUG.toString())));
        return postTagDas.deleteByTag(tagId);
    }
}
