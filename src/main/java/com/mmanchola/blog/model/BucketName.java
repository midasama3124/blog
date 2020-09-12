package com.mmanchola.blog.model;

public enum BucketName {
    IMAGE_STORAGE("blog-image-storage");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String toString() {
        return bucketName;
    }
}
