package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PostMapper implements RowMapper<Post> {

    @Override
    public Post mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                UUID.fromString(resultSet.getString("person_id")),
                resultSet.getInt("parent_id"),
                resultSet.getString("parent_path"),
                resultSet.getString("title"),
                resultSet.getString("metatitle"),
                resultSet.getString("slug"),
                resultSet.getString("status"),
                resultSet.getTimestamp("published_at"),
                resultSet.getTimestamp("updated_at"),
                resultSet.getString("content"),
                resultSet.getString("social_network1"),
                resultSet.getString("social_network2"),
                resultSet.getString("social_network3")
        );
    }
}
