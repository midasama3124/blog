package com.mmanchola.blog.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public Optional<Integer> find(int postId) {
        String sqlQuery = "SELECT * FROM post_category "
                + "WHERE post_id = ?";
        // Retrieve a single object
        Integer categoryId;
        try {
            categoryId = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new Object[]{postId},
                    (resultSet, i) -> resultSet.getInt("category_id")
            );
        } catch (EmptyResultDataAccessException e) {
            categoryId = null;
        }
        return Optional.ofNullable(categoryId);
    }

    @Override
    public int deleteSingle(int postId, int categoryId) {
        String sqlQuery = "DELETE FROM post_category "
                + "WHERE "
                + "post_id = ? "
                + "AND "
                + "category_id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{postId, categoryId});
    }

    @Override
    public int deleteByPost(int postId) {
        String sqlQuery = "DELETE FROM post_category "
                + "WHERE "
                + "post_id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{postId});
    }

    @Override
    public int deleteByCategory(int categoryId) {
        String sqlQuery = "DELETE FROM post_category "
                + "WHERE "
                + "category_id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{categoryId});
    }
}
