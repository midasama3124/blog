package com.mmanchola.blog.dao;

import com.mmanchola.blog.mapper.PersonMapper;
import com.mmanchola.blog.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PersonDataAccessService implements PersonDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Person person) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String sqlQuery = "INSERT INTO person ("
                + "first_name, "
                + "last_name, "
                + "gender, "
                + "age, "
                + "email, "
                + "username, "
                + "password_hash, "
                + "registered_at, "
                + "last_login) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        // Returns number of rows affected
        return jdbcTemplate.update(sqlQuery, person.getFirstName(), person.getLastName(),
                person.getGender().toUpperCase(), person.getAge(), person.getEmail(),
                person.getUsername(), person.getPasswordHash(), current, current);
    }

    @Override
    public List<Person> findAll() {
        String sqlQuery = "SELECT "
                + "id, "
                + "first_name, "
                + "last_name, "
                + "gender, "
                + "age, "
                + "email, "
                + "username, "
                + "password_hash, "
                + "registered_at, "
                + "last_login "
                + "FROM person";
        return jdbcTemplate.query(sqlQuery, new PersonMapper());  // Retrieve multiple results
    }

    @Override
    public Optional<Person> find(String email) {
        String sqlQuery = "SELECT * FROM person WHERE email = ?";
        // Retrieve a single object
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sqlQuery, new Object[]{email}, new PersonMapper())
        );
    }

    @Override
    public Optional<Person> find(UUID id) {
        String sqlQuery = "SELECT * FROM person WHERE id = ?";
        // Retrieve a single object
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, new PersonMapper())
        );
    }

    @Override
    public Optional<UUID> findIdByEmail(String email) {
        String sqlQuery = "SELECT * FROM person "
                + "WHERE email = ?";
        // Retrieve a single object
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        sqlQuery,
                        new Object[]{email},
                        (resultSet, i) -> UUID.fromString(resultSet.getString("id"))
                )
        );
    }

    @Override
    public int updateFirstName(UUID id, String firstName) {
        String sqlQuery = "UPDATE person SET "
                + "first_name = ? "
                + "WHERE id = ?";
        Object[] args = {firstName, id};
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, args);
    }

    @Override
    public int updateLastName(UUID id, String lastName) {
        String sqlQuery = "UPDATE person SET "
                + "last_name = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, lastName, id);
    }

    @Override
    public int updateGender(UUID id, String gender) {
        String sqlQuery = "UPDATE person SET "
                + "gender = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, gender, id);
    }

    @Override
    public int updateAge(UUID id, short age) {
        String sqlQuery = "UPDATE person SET "
                + "age = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, age, id);
    }

    @Override
    public int updateEmail(UUID id, String email) {
        String sqlQuery = "UPDATE person SET "
                + "email = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, email, id);
    }

    @Override
    public int updateUsername(UUID id, String username) {
        String sqlQuery = "UPDATE person SET "
                + "username = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, username, id);
    }

    @Override
    public int updatePasswordHash(UUID id, String passwordHash) {
        String sqlQuery = "UPDATE person SET "
                + "password_hash = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, passwordHash, id);
    }

    @Override
    public int updateLastLogin(UUID id, Timestamp lastLogin) {
        String sqlQuery = "UPDATE person SET "
                + "last_login = ? "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, lastLogin, id);
    }

    @Override
    public int delete(UUID id) {
        String sqlQuery = "DELETE FROM person "
                + "WHERE id = ?";
        // Issue a single SQL update operation (such as an insert, update or delete statement)
        return jdbcTemplate.update(sqlQuery, new Object[]{id});
    }

    @Override
    public boolean isEmailTaken(String email) {
        String sqlQuery = "SELECT EXISTS ("
                + "SELECT 1 FROM person "
                + "WHERE email = ?"
                + ")";
        // Retrieve a single object
        return jdbcTemplate.queryForObject(
                sqlQuery,
                new Object[]{email},
                (resultSet, i) -> resultSet.getBoolean(1)
        );
    }

    @Override
    public boolean isEmailTakenBySomeoneElse(UUID id, String email) {
        String sqlQuery = "SELECT EXISTS ("
                + "SELECT 1 FROM person "
                + "WHERE id <> ? "
                + "AND email = ?"
                + ")";
        return jdbcTemplate.queryForObject(
                sqlQuery,
                new Object[]{id, email},
                (resultSet, i) -> resultSet.getBoolean(1));
    }
}
