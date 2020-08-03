package com.mmanchola.blog.model;

public class PopularPost {
    private final Post post;
    private final int numLikes;

    public PopularPost(Post post, int numLikes) {
        this.post = post;
        this.numLikes = numLikes;
    }

    public Post getPost() {
        return post;
    }

    public int getNumLikes() {
        return numLikes;
    }
}
