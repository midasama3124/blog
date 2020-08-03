package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.Category;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CategoryMapper implements RowMapper<Category> {

  @Override
  public Category mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Category(
        resultSet.getInt("id"),
        resultSet.getInt("parent_id"),
        resultSet.getString("parent_path"),
        resultSet.getString("title"),
        resultSet.getString("metatitle"),
        resultSet.getString("slug"),
        resultSet.getString("content")
    );
  }
}
