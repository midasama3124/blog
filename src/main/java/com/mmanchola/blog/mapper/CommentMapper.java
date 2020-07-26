package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.Comment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CommentMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Comment(
                rs.getLong("id"),
                rs.getInt("post_id"),
                UUID.fromString(rs.getString("person_id")),
                rs.getTimestamp("published_at"),
                rs.getString("content")
        );
    }
}
