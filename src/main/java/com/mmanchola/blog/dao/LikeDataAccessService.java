package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Like;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public class LikeDataAccessService implements LikeDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Like like) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String sqlQuery = "INSERT INTO likes ("
                + "post_id, "
                + "person_id, "
                + "published_at) "
                + "VALUES (?, ?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, like.getPostId(), like.getPersonId(), current);
    }

    @Override
    public int findLikesByPost(int postId) {
        String sqlQuery = "SELECT COUNT(*) " +
                "FROM likes " +
                "WHERE post_id = ?";
        // Retrieve a single object
        return jdbcTemplate.queryForObject(
                sqlQuery,
                new Object[]{postId},
                (resultSet, i) -> resultSet.getInt(1)
        );
    }

    @Override
    public boolean exists(int postId, UUID personId) {
        String sqlQuery = "SELECT EXISTS (" +
                "SELECT 1 FROM likes " +
                "WHERE post_id = ? " +
                "AND person_id = ?" +
                ")";
        // Retrieve a single object
        return jdbcTemplate.queryForObject(
                sqlQuery,
                new Object[]{postId, personId},
                (resultSet, i) -> resultSet.getBoolean(1)
        );
    }

    @Override
    public int delete(long likeId) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{likeId});
    }
}
