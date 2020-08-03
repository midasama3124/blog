package com.mmanchola.blog.model;

public class PopularTag {
    private final Tag tag;
    private final int numPosts;

    public PopularTag(Tag tag, int numPosts) {
        this.tag = tag;
        this.numPosts = numPosts;
    }

    public Tag getTag() {
        return tag;
    }

    public int getNumPosts() {
        return numPosts;
    }
}
