package com.mmanchola.blog.model;

import java.util.List;

public class PostForm {
    private Post post;
    private List<Integer> tagIds;
    private int categoryId;

    public PostForm(Post post, List<Integer> tagIds, int categoryId) {
        this.post = post;
        this.tagIds = tagIds;
        this.categoryId = categoryId;
    }

    public PostForm() {
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
