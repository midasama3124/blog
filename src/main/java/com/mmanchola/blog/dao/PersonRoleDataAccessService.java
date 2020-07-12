package com.mmanchola.blog.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PersonRoleDataAccessService implements PersonRoleDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonRoleDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

  @Override
  public int save(UUID personId, short roleId) {
    String sqlQuery = "INSERT INTO person_role ("
        + "person_id, "
        + "role_id) "
        + "VALUES (?, ?)";
    // Issue a single SQL update operation (such as an insert, update or delete statement)
    // Returns number of rows affected
    return jdbcTemplate.update(sqlQuery, personId, roleId);
  }

    @Override
    public List<Short> findRoles(UUID personId) {
        String sqlQuery = "SELECT "
                + "role_id "
                + "FROM person_role "
                + "WHERE "
                + "person_id = ?";
        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{personId},
                (resultSet, i) -> resultSet.getShort("role_id")
        );  // Retrieve multiple results
    }

    @Override
    public List<UUID> findPeople(short roleId) {
        String sqlQuery = "SELECT "
                + "person_id "
                + "FROM person_role "
                + "WHERE "
                + "role_id = ?";
        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{roleId},
                (resultSet, i) -> UUID.fromString(resultSet.getString("person_id"))
        );  // Retrieve multiple results
    }

    @Override
    public int delete(UUID personId, short roleId) {
        String sqlQuery = "DELETE FROM person_role "
                + "WHERE "
                + "person_id = ? "
                + "AND "
                + "role_id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{personId, roleId});
  }
}
