package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.CommentMapper;
import com.mmanchola.blog.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CommentDataAccessService implements CommentDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Comment comment) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String sqlQuery = "INSERT INTO comment ("
                + "post_id, "
                + "person_id, "
                + "published_at, "
                + "content) "
                + "VALUES (?, ?, ?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, comment.getPostId(), comment.getPersonId(),
                current, comment.getContent());
    }

    @Override
    public List<Comment> findAll() {
        String sqlQuery = "SELECT "
                + "* "
                + "FROM "
                + "comment";
        return jdbcTemplate.query(sqlQuery, new CommentMapper());  // Retrieve multiple results
    }

    @Override
    public Optional<Comment> find(long commentId) {
        String sqlQuery = "SELECT * FROM comment "
                + "WHERE id = ?";
        // Retrieve a single object
        Comment comment;
        try {
            comment = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new Object[]{commentId},
                    new CommentMapper()
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            comment = null;
        }
        return Optional.ofNullable(
                comment
        );
    }

    @Override
    public Optional<Comment> find(int postId, UUID personId) {
        String sqlQuery = "SELECT * FROM comment " +
                "WHERE postId = ? " +
                "AND " +
                "personId = ?";
        // Retrieve a single object
        Comment comment;
        try {
            comment = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new Object[]{postId, personId},
                    new CommentMapper()
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            comment = null;
        }
        return Optional.ofNullable(
                comment
        );
    }

    @Override
    public List<Comment> findByPost(int postId) {
        String sqlQuery = "SELECT * FROM comment " +
                "WHERE post_id = ? " +
                "ORDER BY published_at DESC " +
                "NULLS LAST";
        return jdbcTemplate.query(sqlQuery,
                new Object[]{postId},
                new CommentMapper());  // Retrieve multiple results
    }

    @Override
    public List<Comment> findByReaderAndPost(UUID personId, int postId) {
        String sqlQuery = "SELECT * FROM comment " +
                "WHERE person_id = ? " +
                "AND post_id = ? " +
                "ORDER BY published_at DESC " +
                "NULLS LAST";
        return jdbcTemplate.query(sqlQuery,
                new Object[]{personId, postId},
                new CommentMapper());  // Retrieve multiple results
    }

    @Override
    public boolean exists(long commentId) {
        String sqlQuery = "SELECT EXISTS ("
                + "SELECT 1 FROM comment "
                + "WHERE id = ?"
                + ")";
        // Retrieve a single object
        return jdbcTemplate.queryForObject(
                sqlQuery,
                new Object[]{commentId},
                (resultSet, i) -> resultSet.getBoolean(1)
        );
    }

    @Override
    public int delete(long commentId) {
        String sqlQuery = "DELETE FROM comment " +
                "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{commentId});
    }
}
