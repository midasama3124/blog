package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Person;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonDao {
  // C: Create
  int save(Person person);

  // R: Read
  List<Person> findAll();
  Optional<Person> findByEmail(String email);
  UUID findIdByEmail(String email);

  // U: Update
  int updateFirstName(UUID id, String firstName);
  int updateLastName(UUID id, String lastName);
  int updateGender (UUID id, String gender);
  int updateAge(UUID id, short age);
  int updateEmail(UUID id, String email);
  int updatePasswordHash(UUID id, String passwordHash);
  int updateLastLogin(UUID id, Timestamp lastLogin);

  // D: Delete
  int delete(UUID id);

  // Check constraints
  boolean isEmailTaken(String email);
  boolean isEmailTakenBySomeoneElse(UUID id, String email);
}
