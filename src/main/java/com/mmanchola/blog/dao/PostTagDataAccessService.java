package com.mmanchola.blog.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostTagDataAccessService implements PostTagDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PostTagDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(int postId, int tagId) {
        String sqlQuery = "INSERT INTO post_tag ("
                + "post_id, "
                + "tag_id) "
                + "VALUES (?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, postId, tagId);
    }

    @Override
    public List<Integer> findByPost(int postId) {
        String sqlQuery = "SELECT "
                + "tag_id "
                + "FROM post_tag "
                + "WHERE "
                + "post_id = ?";
        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{postId},
                (resultSet, i) -> resultSet.getInt("tag_id")
        ); // Retrieve multiple results
    }

    @Override
    public List<Integer> findByTag(int tagId) {
        String sqlQuery = "SELECT "
                + "post_id "
                + "FROM post_tag "
                + "WHERE "
                + "tag_id = ?";
        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{tagId},
                (resultSet, i) -> resultSet.getInt("post_id")
        ); // Retrieve multiple results
    }

    @Override
    public int deleteSingle(int postId, int tagId) {
        String sqlQuery = "DELETE FROM post_tag "
                + "WHERE "
                + "post_id = ? "
                + "AND "
                + "tag_id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{postId, tagId});
    }

    @Override
    public int deleteByPost(int postId) {
        String sqlQuery = "DELETE FROM post_tag "
                + "WHERE "
                + "post_id = ? ";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{postId});
    }

    @Override
    public int deleteByTag(int tagId) {
        String sqlQuery = "DELETE FROM post_tag "
                + "WHERE "
                + "tag_id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{tagId});
    }
}
