package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.PopularTag;
import com.mmanchola.blog.model.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PopularTagMapper implements RowMapper<PopularTag> {
    @Override
    public PopularTag mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PopularTag(
                new Tag(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("metatitle"),
                        rs.getString("slug"),
                        rs.getString("content")
                ), rs.getInt("num_tags")
        );
    }
}
