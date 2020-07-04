package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.CategoryMapper;
import com.mmanchola.blog.model.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDataAccessService implements CategoryDao {
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public CategoryDataAccessService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int saveParent(Category category) {
    String sqlQuery = "INSERT INTO category ("
        + "title, "
        + "metatitle, "
        + "slug, "
        + "content) "
        + "VALUES (?, ?, ?, ?)";
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    // Returns number of rows affected
    return jdbcTemplate.update(sqlQuery, category.getTitle(),
        category.getMetatitle(), category.getSlug(), category.getContent());
  }

  @Override
  public int saveChild(Category category) {
    String sqlQuery = "INSERT INTO category ("
        + "parent_id, "
        + "title, "
        + "metatitle, "
        + "slug, "
        + "content) "
        + "VALUES (?, ?, ?, ?, ?)";
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    // Returns number of rows affected
    return jdbcTemplate.update(sqlQuery, category.getParentId(), category.getTitle(),
        category.getMetatitle(), category.getSlug(), category.getContent());
  }

  @Override
  public List<Category> findAll() {
    String sqlQuery = "SELECT "
        + "* "
        + "FROM "
        + "category";
    return jdbcTemplate.query(sqlQuery, new CategoryMapper());  // Retrieve multiple results
  }

  @Override
  public Optional<Category> findBySlug(String slug) {
    String sqlQuery = "SELECT "
        + "* "
        + "FROM "
        + "category "
        + "WHERE "
        + "slug = ?";
    // Retrieve a single object
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(sqlQuery, new Object[] {slug}, new CategoryMapper())
    );
  }

  @Override
  public Optional<Integer> findIdBySlug(String slug) {
    String sqlQuery = "SELECT * FROM category "
        + "WHERE slug = ?";
    // Retrieve a single object
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            sqlQuery,
            new Object[] {slug},
            (resultSet, i) -> resultSet.getInt("id")
        )
    );
  }

  @Override
  public boolean isSlugTaken(String slug) {
    String sqlQuery = "SELECT EXISTS ("
        + "SELECT 1 FROM category "
        + "WHERE slug = ?"
        + ")";
    // Retrieve a single object
    return jdbcTemplate.queryForObject(
        sqlQuery,
        new Object[] {slug},
        (resultSet, i) -> resultSet.getBoolean(1)
    );
  }

  @Override
  public boolean isSlugTakenByOther(String newSlug, int categoryId) {
    String sqlQuery = "SELECT EXISTS ("
        + "SELECT 1 FROM category "
        + "WHERE id <> ? "
        + "AND slug = ?"
        + ")";
    return jdbcTemplate.queryForObject(
        sqlQuery,
        new Object[] {categoryId, newSlug},
        (resultSet, i) -> resultSet.getBoolean(1));
  }

  @Override
  public int updateParentId(int categoryId, int parentId) {
    String sqlQuery = "UPDATE category SET "
        + "parent_id = ? "
        + "WHERE id = ?";
    Object[] args = {parentId, categoryId};
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    return jdbcTemplate.update(sqlQuery, args);
  }

  @Override
  public int updateTitle(int categoryId, String title) {
    String sqlQuery = "UPDATE category SET "
        + "title = ? "
        + "WHERE id = ?";
    Object[] args = {title, categoryId};
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    return jdbcTemplate.update(sqlQuery, args);
  }

  @Override
  public int updateMetatitle(int categoryId, String metatitle) {
    String sqlQuery = "UPDATE category SET "
        + "metatitle = ? "
        + "WHERE id = ?";
    Object[] args = {metatitle, categoryId};
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    return jdbcTemplate.update(sqlQuery, args);
  }

  @Override
  public int updateSlug(int categoryId, String slug) {
    String sqlQuery = "UPDATE category SET "
        + "slug = ? "
        + "WHERE id = ?";
    Object[] args = {slug, categoryId};
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    return jdbcTemplate.update(sqlQuery, args);
  }

  @Override
  public int updateContent(int categoryId, String content) {
    String sqlQuery = "UPDATE category SET "
        + "content = ? "
        + "WHERE id = ?";
    Object[] args = {content, categoryId};
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    return jdbcTemplate.update(sqlQuery, args);
  }

  @Override
  public int delete(int categoryId) {
    String sqlQuery = "DELETE FROM category "
        + "WHERE id = ?";
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    return jdbcTemplate.update(sqlQuery, new Object[] {categoryId});
  }
}
