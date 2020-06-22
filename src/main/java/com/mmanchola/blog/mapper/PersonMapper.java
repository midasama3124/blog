package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.Person;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class PersonMapper implements RowMapper<Person> {

  @Override
  public Person mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Person(
        UUID.fromString(resultSet.getString("id")),
        resultSet.getString("first_name"),
        resultSet.getString("last_name"),
        resultSet.getString("gender"),
        resultSet.getShort("age"),
        resultSet.getString("email"),
        resultSet.getString("password_hash"),
        resultSet.getTimestamp("registered_at"),
        resultSet.getTimestamp("last_login")
    );
  }
}
