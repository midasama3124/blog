package com.mmanchola.blog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import java.util.UUID;

public class Person {

  private final UUID id;
  private final String firstName;
  private final String lastName;
  private final String gender;
  private final short age;
  private final String username;
  private final String email;
  private final String passwordHash;
  private final Timestamp registeredAt;
  private final Timestamp lastLogin;

  public Person(@JsonProperty("id") UUID id,
                @JsonProperty("first_name") String firstName,
                @JsonProperty("last_name") String lastName,
                @JsonProperty("gender") String gender,
                @JsonProperty("age") short age,
                @JsonProperty("username") String username,
                @JsonProperty("email") String email,
                @JsonProperty("password") String passwordHash,
                @JsonProperty("registered_at") Timestamp registeredAt,
                @JsonProperty("last_login") Timestamp lastLogin) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.age = age;
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.registeredAt = registeredAt;
    this.lastLogin = lastLogin;
  }

  public UUID getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getGender() {
    return gender;
  }

  public short getAge() {
    return age;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Timestamp getRegisteredAt() {
    return registeredAt;
  }

  public Timestamp getLastLogin() {
    return lastLogin;
  }

}
