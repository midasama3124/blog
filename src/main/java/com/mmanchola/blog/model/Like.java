package com.mmanchola.blog.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Like {
    private long id;
    private int postId;
    private UUID personId;
    private Timestamp publishedAt;

    public Like(long id, int postId, UUID personId, Timestamp publishedAt) {
        this.id = id;
        this.postId = postId;
        this.personId = personId;
        this.publishedAt = publishedAt;
    }

    public Like() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    public Timestamp getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Timestamp publishedAt) {
        this.publishedAt = publishedAt;
    }
}
