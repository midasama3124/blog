package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.PopularPostMapper;
import com.mmanchola.blog.mapper.PostMapper;
import com.mmanchola.blog.model.PopularPost;
import com.mmanchola.blog.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mmanchola.blog.model.PostStatus.PUBLISHED;

@Repository
public class PostDataAccessService implements PostDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PostDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int saveParent(Post post) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String sqlQuery = "INSERT INTO post ("
                + "person_id, "
                + "title, "
                + "metatitle, "
                + "slug, "
                + "status, "
                + "updated_at, "
                + "content, "
                + "social_network1, "
                + "social_network2, "
                + "social_network3) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, post.getPersonId(), post.getTitle(),
                post.getMetatitle(), post.getSlug(), post.getStatus(),
                current, post.getContent(), post.getSocialNetwork1(), post.getSocialNetwork2(),
                post.getSocialNetwork3());
    }

    @Override
    public int saveChild(Post post) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String sqlQuery = "INSERT INTO post ("
                + "person_id, "
                + "parent_id, "
                + "title, "
                + "metatitle, "
                + "slug, "
                + "status, "
                + "updated_at, "
                + "content, "
                + "social_network1, "
                + "social_network2, "
                + "social_network3) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, post.getPersonId(), post.getParentId(),
                post.getTitle(), post.getMetatitle(), post.getSlug(), post.getStatus(),
                current, post.getContent(), post.getSocialNetwork1(), post.getSocialNetwork2(),
                post.getSocialNetwork3());
    }

    @Override
    public List<Post> findAll() {
        String sqlQuery = "SELECT "
                + "* "
                + "FROM "
                + "post";
        return jdbcTemplate.query(sqlQuery, new PostMapper());  // Retrieve multiple results
    }

    @Override
    public Optional<Post> find(int id) {
        String sqlQuery = "SELECT * FROM post "
                + "WHERE id = ?";
        // Retrieve a single object
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, new PostMapper())
        );
    }

    @Override
    public List<Post> findByStatus(String status) {
        String sqlQuery = "SELECT * FROM post "
                + "WHERE status = ?";
        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{status},
                new PostMapper());        // Retrieve multiple results
    }

    @Override
    public Optional<Post> findBySlug(String slug) {
        String sqlQuery = "SELECT * FROM post "
                + "WHERE slug = ?";
        Post post;
        try {
            post = jdbcTemplate.queryForObject(sqlQuery, new Object[]{slug}, new PostMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            post = null;
        }
        // Retrieve a single object
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findMostRecent() {
        return findMostRecent(1);
    }

    @Override
    public List<Post> findMostRecent(int numPosts) {
        String sqlQuery = "SELECT " +
                "* " +
                "FROM " +
                "post " +
                "WHERE status = ? " +
                "ORDER BY published_at DESC " +
                "NULLS LAST LIMIT ?";
        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{PUBLISHED.toString(), numPosts},
                new PostMapper()
        );
    }

    @Override
    public Optional<Integer> findIdBySlug(String slug) {
        String sqlQuery = "SELECT * FROM post "
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
    public List<PopularPost> findPopular(int numPosts) {
        String sqlQuery = "SELECT post.*, COUNT(likes.post_id) AS num_likes " +
                "FROM post LEFT JOIN likes " +
                "ON (post.id = likes.post_id) " +
                "GROUP BY post.id " +
                "ORDER BY num_likes DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{numPosts},
                new PopularPostMapper()
        );
    }

    @Override
    public Optional<String> findSlugById(int postId) {
        String sqlQuery = "SELECT * FROM post "
                + "WHERE id = ?";
        // Retrieve a single object
        String slug;
        try {
            slug = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new Object[]{postId},
                    (resultSet, i) -> resultSet.getString("slug")
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            slug = null;
        }
        return Optional.ofNullable(slug);
    }

    @Override
    public boolean isSlugTaken(String slug) {
        String sqlQuery = "SELECT EXISTS ("
                + "SELECT 1 FROM post "
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
    public boolean isSlugTakenByOther(String newSlug, int postId) {
        String sqlQuery = "SELECT EXISTS ("
                + "SELECT 1 FROM post "
                + "WHERE id <> ? "
                + "AND slug = ?"
                + ")";
        return jdbcTemplate.queryForObject(
                sqlQuery,
                new Object[]{postId, newSlug},
                (resultSet, i) -> resultSet.getBoolean(1));
    }

    @Override
    public int updatePersonId(int postId, UUID personId) {
        String sqlQuery = "UPDATE post SET "
                + "person_id = ? "
                + "WHERE id = ?";
        Object[] args = {personId, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateParentId(int postId, int parentId) {
        String sqlQuery = "UPDATE post SET "
                + "parent_id = ? "
                + "WHERE id = ?";
        Object[] args = {parentId, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateTitle(int postId, String title) {
        String sqlQuery = "UPDATE post SET "
                + "title = ? "
                + "WHERE id = ?";
        Object[] args = {title, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateMetatitle(int postId, String metatitle) {
        String sqlQuery = "UPDATE post SET "
                + "metatitle = ? "
                + "WHERE id = ?";
        Object[] args = {metatitle, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateSlug(int postId, String slug) {
        String sqlQuery = "UPDATE post SET "
                + "slug = ? "
                + "WHERE id = ?";
        Object[] args = {slug, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateStatus(int postId, String status) {
        String sqlQuery = "UPDATE post SET "
                + "status = ? "
                + "WHERE id = ?";
        Object[] args = {status, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updatePublishedAt(int postId, Timestamp publishedAt) {
        String sqlQuery = "UPDATE post SET "
                + "published_at = ? "
                + "WHERE id = ?";
        Object[] args = {publishedAt, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateUpdatedAt(int postId, Timestamp updatedAt) {
        String sqlQuery = "UPDATE post SET "
                + "updated_at = ? "
                + "WHERE id = ?";
        Object[] args = {updatedAt, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateContent(int postId, String content) {
        String sqlQuery = "UPDATE post SET "
                + "content = ? "
                + "WHERE id = ?";
        Object[] args = {content, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateSocialNetwork1(int postId, String socialNetwork1) {
        String sqlQuery = "UPDATE post SET "
                + "social_network1 = ? "
                + "WHERE id = ?";
        Object[] args = {socialNetwork1, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateSocialNetwork2(int postId, String socialNetwork2) {
        String sqlQuery = "UPDATE post SET "
                + "social_network2 = ? "
                + "WHERE id = ?";
        Object[] args = {socialNetwork2, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateSocialNetwork3(int postId, String socialNetwork3) {
        String sqlQuery = "UPDATE post SET "
                + "social_network3 = ? "
                + "WHERE id = ?";
        Object[] args = {socialNetwork3, postId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int delete(int postId) {
        String sqlQuery = "DELETE FROM post " +
                "WHERE " +
                "id = ? ";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{postId});
    }
}
