package com.mmanchola.blog.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.UUID;

public class Person {

  private UUID id;
  private String firstName;
  private String lastName;
  private String gender;
  private short age;
  private String email;
  private String passwordHash;
  private Timestamp registeredAt;
  private Timestamp lastLogin;

  public Person(@JsonProperty("id") UUID id,
                @JsonProperty("first_name") String firstName,
                @JsonProperty("last_name") String lastName,
                @JsonProperty("gender") String gender,
                @JsonProperty("age") short age,
                @JsonProperty("email") String email,
                @JsonProperty("password") String passwordHash,
                @JsonProperty("registered_at") Timestamp registeredAt,
                @JsonProperty("last_login") Timestamp lastLogin) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.age = age;
    this.email = email;
    this.passwordHash = passwordHash;
    this.registeredAt = registeredAt;
    this.lastLogin = lastLogin;
  }

  public Person() { }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public short getAge() {
    return age;
  }

  public void setAge(short age) {
    this.age = age;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Timestamp getRegisteredAt() {
    return registeredAt;
  }

  public void setRegisteredAt(Timestamp registeredAt) {
    this.registeredAt = registeredAt;
  }

  public Timestamp getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Timestamp lastLogin) {
    this.lastLogin = lastLogin;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Person))
      return false;
    Person other = (Person) o;
    return this.email.equals(other.getEmail()) &&
            this.firstName.equals(other.getFirstName()) &&
            this.lastName.equals(other.getLastName()) &&
            this.gender.equals(other.getGender()) &&
            this.age == other.getAge();
  }

  @Override
  public String toString() {
    String msg = new String();
    if (this.id != null) msg += "ID: " + this.id + "\n";
    if (this.firstName != null) msg += "First name: " + this.firstName + "\n";
    if (this.lastName != null) msg += "Last name: " + this.lastName + "\n";
    if (this.gender != null) msg += "Gender: " + this.gender + "\n";
    msg += "Age: " + this.age + "\n";
    if (this.email != null) msg += "Email: " + this.email + "\n";
    if (this.passwordHash != null) msg += "Password Hash: " + this.passwordHash + "\n";
    if (this.registeredAt != null) msg += "Registration: " + this.registeredAt + "\n";
    if (this.lastLogin != null) msg += "Last Login: " + this.lastLogin + "\n";
    return msg;
  }
}
