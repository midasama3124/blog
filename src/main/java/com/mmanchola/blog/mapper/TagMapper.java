package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.Tag;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TagMapper implements RowMapper<Tag> {

  @Override
  public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Tag(
        resultSet.getInt("id"),
        resultSet.getString("title"),
        resultSet.getString("metatitle"),
        resultSet.getString("slug"),
        resultSet.getString("content")
    );
  }
}
