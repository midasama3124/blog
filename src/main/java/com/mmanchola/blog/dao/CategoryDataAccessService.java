package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.CategoryMapper;
import com.mmanchola.blog.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        String sqlQuery = "SELECT * FROM category";
        return jdbcTemplate.query(sqlQuery, new CategoryMapper());  // Retrieve multiple results
    }

    @Override
    public List<Category> findParents() {
        String sqlQuery = "SELECT * FROM category " +
                "WHERE parent_path = 'root' " +
                "ORDER BY title asc";
        return jdbcTemplate.query(sqlQuery, new CategoryMapper());  // Retrieve multiple results
    }

    @Override
    public List<Category> findChildren(int parentId) {
        String sqlQuery = "SELECT * FROM category " +
                "WHERE parent_id = ? " +
                "ORDER BY title asc";
        return jdbcTemplate.query(sqlQuery,
                new Object[]{parentId},
                new CategoryMapper()
        );  // Retrieve multiple results
    }

    @Override
    public Optional<Category> find(int id) {
        String sqlQuery = "SELECT * FROM category "
                + "WHERE id = ?";
        // Retrieve a single object
        Category category;
        try {
            category = jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, new CategoryMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            category = null;
        }
        return Optional.ofNullable(category);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        String sqlQuery = "SELECT * FROM category "
                + "WHERE slug = ?";
        // Retrieve a single object
        Category category;
        try {
            category = jdbcTemplate.queryForObject(sqlQuery, new Object[]{slug}, new CategoryMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            category = null;
        }
        return Optional.ofNullable(category);
    }

    @Override
    public Optional<Integer> findIdBySlug(String slug) {
        String sqlQuery = "SELECT * FROM category "
                + "WHERE slug = ?";
        // Retrieve a single object
        Integer id;
        try {
            id = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new Object[]{slug},
                    (resultSet, i) -> resultSet.getInt("id")
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            id = null;
        }
        return Optional.ofNullable(id);
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
                new Object[]{slug},
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
                new Object[]{categoryId, newSlug},
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
    public int updateParentIdToNull(int categoryId) {
        String sqlQuery = "UPDATE category SET "
                + "parent_id = NULL "
                + "WHERE id = ?";
        Object[] args = {categoryId};
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
        return jdbcTemplate.update(sqlQuery, new Object[]{categoryId});
    }
}
