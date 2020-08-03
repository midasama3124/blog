package com.mmanchola.blog.model;

import java.util.HashSet;
import java.util.Set;

public enum PostStatus {
    DRAFT,
    PUBLISHED,
    OUTDATED;

    public String getStatus() {
        return this.name().toLowerCase();
    }

    // Better approach if enum is large
    public static Set<String> getEnums() {
        Set<String> values = new HashSet<String>();
        for (PostStatus s : PostStatus.values())
            values.add(s.getStatus());
        return values;
    }

    public static boolean contains(String status) {
        return PostStatus.getEnums().contains(status);
    }

    @Override
    public String toString() {
        return this.getStatus();
    }
}
