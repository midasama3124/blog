package com.mmanchola.blog.mapper;

import com.mmanchola.blog.model.Role;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RoleMapper implements RowMapper<Role> {

  @Override
  public Role mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Role(
        resultSet.getShort("id"),
        resultSet.getString("name"),
        resultSet.getString("description")
    );
  }
}
