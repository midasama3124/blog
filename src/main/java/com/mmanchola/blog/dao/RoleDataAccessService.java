package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.RoleMapper;
import com.mmanchola.blog.model.Role;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    // Retrieve a single object
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(sqlQuery, new Object[] {name}, new RoleMapper())
    );
  }

  @Override
  public String findNameById(short id) {
    String sqlQuery = "SELECT * FROM role "
        + "WHERE id = ?";
    // Retrieve a single object
    return jdbcTemplate.queryForObject(
        sqlQuery,
        new Object[] {id},
        (resultSet, i) -> resultSet.getString("name")
    );
  }

  @Override
  public short findIdByName(String name) {
    String sqlQuery = "SELECT * FROM role "
        + "WHERE name = ?";
    // Retrieve a single object
    return jdbcTemplate.queryForObject(
        sqlQuery,
        new Object[] {name},
        (resultSet, i) -> resultSet.getShort("id")
    );
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
    return jdbcTemplate.update(sqlQuery, new Object[] {name});
  }
}
