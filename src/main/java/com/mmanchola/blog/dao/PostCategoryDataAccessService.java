package com.mmanchola.blog.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostCategoryDataAccessService implements PostCategoryDao {
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public PostCategoryDataAccessService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int save(int postId, int categoryId) {
    String sqlQuery = "INSERT INTO post_category ("
        + "post_id, "
        + "category_id) "
        + "VALUES (?, ?)";
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    // Returns number of rows affected
    return jdbcTemplate.update(sqlQuery, postId, categoryId);
  }

  @Override
  public List<Integer> find(int postId) {
    String sqlQuery = "SELECT "
        + "category_id "
        + "FROM post_category "
        + "WHERE "
        + "post_id = ?";
    return jdbcTemplate.query(
        sqlQuery,
        new Object[] {postId},
        (resultSet, i) -> resultSet.getInt("category_id")
    ); // Retrieve multiple results
  }

  @Override
  public int delete(int postId, int categoryId) {
    String sqlQuery = "DELETE FROM post_category "
        + "WHERE "
        + "post_id = ? "
        + "AND "
        + "category_id = ?";
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    return jdbcTemplate.update(sqlQuery, new Object[] {postId, categoryId});
  }
}
