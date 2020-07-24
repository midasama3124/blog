package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.TagMapper;
import com.mmanchola.blog.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TagDataAccessService implements TagDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Tag tag) {
        String sqlQuery = "INSERT INTO tag ("
                + "title, "
                + "metatitle, "
                + "slug, "
                + "content) "
                + "VALUES (?, ?, ?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, tag.getTitle(),
                tag.getMetatitle(), tag.getSlug(), tag.getContent());
    }

    @Override
    public List<Tag> findAll() {
        String sqlQuery = "SELECT "
                + "* "
                + "FROM "
                + "tag";
        return jdbcTemplate.query(sqlQuery, new TagMapper());  // Retrieve multiple results
    }

    @Override
    public Optional<Tag> find(int id) {
        String sqlQuery = "SELECT "
                + "* "
                + "FROM "
                + "tag "
                + "WHERE "
                + "id = ?";
        // Retrieve a single object
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, new TagMapper())
        );
    }

    @Override
    public Optional<Tag> findBySlug(String slug) {
        String sqlQuery = "SELECT "
                + "* "
                + "FROM "
                + "tag "
                + "WHERE "
                + "slug = ?";
        // Retrieve a single object
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sqlQuery, new Object[]{slug}, new TagMapper())
        );
    }

    @Override
    public Optional<Integer> findIdBySlug(String slug) {
        String sqlQuery = "SELECT * FROM tag "
                + "WHERE slug = ?";
        // Retrieve a single object
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        sqlQuery,
                        new Object[]{slug},
                        (resultSet, i) -> resultSet.getInt("id")
                )
        );
    }

    @Override
    public boolean isSlugTaken(String slug) {
        String sqlQuery = "SELECT EXISTS ("
                + "SELECT 1 FROM tag "
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
    public boolean isSlugTakenByOther(String newSlug, int tagId) {
        String sqlQuery = "SELECT EXISTS ("
                + "SELECT 1 FROM tag "
                + "WHERE id <> ? "
                + "AND slug = ?"
                + ")";
        return jdbcTemplate.queryForObject(
                sqlQuery,
                new Object[]{tagId, newSlug},
                (resultSet, i) -> resultSet.getBoolean(1));
    }

    @Override
    public int updateTitle(int tagId, String title) {
        String sqlQuery = "UPDATE tag SET "
                + "title = ? "
                + "WHERE id = ?";
        Object[] args = {title, tagId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateMetatitle(int tagId, String metatitle) {
        String sqlQuery = "UPDATE tag SET "
                + "metatitle = ? "
                + "WHERE id = ?";
        Object[] args = {metatitle, tagId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateSlug(int tagId, String slug) {
        String sqlQuery = "UPDATE tag SET "
                + "slug = ? "
                + "WHERE id = ?";
        Object[] args = {slug, tagId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateContent(int tagId, String content) {
        String sqlQuery = "UPDATE tag SET "
                + "content = ? "
                + "WHERE id = ?";
        Object[] args = {content, tagId};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int delete(int tagId) {
        String sqlQuery = "DELETE FROM tag "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{tagId});
    }
}
