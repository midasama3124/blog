package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.RoleMapper;
import com.mmanchola.blog.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleDataAccessService implements RoleDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Role role) {
        String sqlQuery = "INSERT INTO role ("
                + "name, "
                + "description) "
                + "VALUES (?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, role.getName(), role.getDescription());
    }

    @Override
    public Optional<Role> findByName(String name) {
        String sqlQuery = "SELECT * FROM role WHERE name = ?";
        Role role;
        try {
            // Retrieve a single object
            role = jdbcTemplate.queryForObject(sqlQuery, new Object[]{name}, new RoleMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            role = null;
        }
        return Optional.ofNullable(role);
    }

    @Override
    public Optional<String> findNameById(short id) {
        String sqlQuery = "SELECT * FROM role "
                + "WHERE id = ?";
        String name;
        try {
            // Retrieve a single object
            name = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new Object[]{id},
                    (resultSet, i) -> resultSet.getString("name")
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            name = null;
        }
        return Optional.ofNullable(name);
    }

    @Override
    public Optional<Short> findIdByName(String name) {
        String sqlQuery = "SELECT * FROM role "
                + "WHERE name = ?";
        Short id;
        try {
            // Retrieve a single object
            id = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new Object[]{name},
                    (resultSet, i) -> resultSet.getShort("id")
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            id = null;
        }
        return Optional.ofNullable(id);
    }

    @Override
    public int updateName(short id, String name) {
        String sqlQuery = "UPDATE role SET "
                + "name = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, name, id);
    }

    @Override
    public int updateDescription(short id, String description) {
        String sqlQuery = "UPDATE role SET "
                + "description = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, description, id);
    }

    @Override
    public int deleteByName(String name) {
        String sqlQuery = "DELETE FROM role "
                + "WHERE name = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{name});
    }
}
