package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Person;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonDao {
  // C: Create
  int create(UUID id, Person person);

  // R: Read
  List<Person> readAll();
  Optional<Person> readById(UUID id);

  // U: Update
  int updateFirstName(UUID id, String firstName);
  int updateLastName(UUID id, String lastName);
  int updateGender (UUID id, String gender);
  int updateAge(UUID id, short age);
  int updateUsername(UUID id, String username);
  int updateEmail(UUID id, String email);
  int updatePasswordHash(UUID id, String passwordHash);
  int updateLastLogin(UUID id, Timestamp lastLogin);

  // D: Delete
  int deleteById(UUID id);

  // Check constraints
  boolean isEmailTaken(String email);
  boolean isEmailTakenBySomeoneElse(UUID id, String email);
  boolean isUsernameTaken(String username);
  boolean isUsernameTakenBySomeoneElse(UUID id, String username);
}
