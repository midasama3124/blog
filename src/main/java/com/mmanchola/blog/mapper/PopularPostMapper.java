package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.PopularPost;
import com.mmanchola.blog.model.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PopularPostMapper implements RowMapper<PopularPost> {
    @Override
    public PopularPost mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PopularPost(
                new Post(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("person_id")),
                        rs.getInt("parent_id"),
                        rs.getString("parent_path"),
                        rs.getString("title"),
                        rs.getString("metatitle"),
                        rs.getString("slug"),
                        rs.getString("status"),
                        rs.getTimestamp("published_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("content"),
                        rs.getString("social_network1"),
                        rs.getString("social_network2"),
                        rs.getString("social_network3")
                ),
                rs.getInt("num_likes")
        );
    }
}
