package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.util.EmailValidator;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonService {
  private final PersonDataAccessService dataAccessService;
  private final EmailValidator emailValidator;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public PersonService(PersonDataAccessService dataAccessService,
      EmailValidator emailValidator,
      PasswordEncoder passwordEncoder) {
    this.dataAccessService = dataAccessService;
    this.emailValidator = emailValidator;
    this.passwordEncoder = passwordEncoder;
  }

  // Add new person to database
  public int add(Person person) {
    // Check first name
    Optional.ofNullable(person.getFirstName())
        .filter(name -> !StringUtils.isEmpty(name))
        .map(StringUtils::capitalize)
        .ifPresent(firstName -> person.setFirstName(firstName));

    // Check last name
    Optional.ofNullable(person.getLastName())
        .filter(name -> !StringUtils.isEmpty(name))
        .map(StringUtils::capitalize)
        .ifPresent(lastName -> person.setLastName(lastName));

    // Check gender
    Optional.ofNullable(person.getGender())
        .filter(gender -> !StringUtils.isEmpty(gender))
        .ifPresent(gender -> {
          gender = gender.toUpperCase();
          if (gender.equals("MALE")) person.setGender("MALE");
          else if (gender.equals("FEMALE")) person.setGender("FEMALE");
          else person.setGender("OTHER");
        });

    // Check email
    Optional.ofNullable(person.getEmail())
        .filter(email -> !StringUtils.isEmpty(email))
        .ifPresent(email -> {
          if (!emailValidator.test(email)) {
            throw new ApiRequestException(email + " is not valid");
          }
          boolean taken = dataAccessService.isEmailTaken(email);
          if (taken) throw new ApiRequestException(email + " is already taken");
        });

    // Check password hash
    Optional.ofNullable(person.getPasswordHash())
        .filter(password -> !StringUtils.isEmpty(password))
        .ifPresent(passwordHash ->
            // Encrypt password
            person.setPasswordHash(passwordEncoder.encode(passwordHash)
        ));

    return dataAccessService.save(person);
  }

  public List<Person> getAllPeople() { return dataAccessService.findAll(); }

  public Optional<Person> getByEmail(String email) {
    return dataAccessService.findByEmail(email);
  }

  public void update(String username, Person person) {
    // Retrieve id from database
    UUID id = dataAccessService.findIdByEmail(username);
    // Check first name
    Optional.ofNullable(person.getFirstName())
        .filter(name -> !StringUtils.isEmpty(name))
        .map(StringUtils::capitalize)
        .ifPresent(firstName ->dataAccessService.updateFirstName(id, firstName));

    // Check last name
    Optional.ofNullable(person.getLastName())
        .filter(name -> !StringUtils.isEmpty(name))
        .map(StringUtils::capitalize)
        .ifPresent(lastName ->dataAccessService.updateLastName(id, lastName));

    // Check gender
    Optional.ofNullable(person.getGender())
        .filter(gender -> !StringUtils.isEmpty(gender))
        .ifPresent(gender -> {
          gender = gender.toUpperCase();
          if (gender.equals("MALE")) dataAccessService.updateGender(id, "MALE");
          else if (gender.equals("FEMALE")) dataAccessService.updateGender(id, "FEMALE");
          else dataAccessService.updateGender(id, "OTHER");
        });

    // Check age
    Optional.ofNullable(person.getAge())
        .ifPresent(age -> dataAccessService.updateAge(id, age));

    // Check username
//    Optional.ofNullable(person.getUsername())
//        .filter(username -> !StringUtils.isEmpty(username))
//        .map(String::toLowerCase)
//        .ifPresent(username -> {
//          username = username.replaceAll("\\s", "");  // Remove white spaces from username
//          if (dataAccessService.isUsernameTakenBySomeoneElse(id, username)) {
//            throw new ApiRequestException(username + " is already taken");
//          }
//          dataAccessService.updateUsername(id, username);
//        });

    // Check email
    Optional.ofNullable(person.getEmail())
        .filter(email -> !StringUtils.isEmpty(email))
        .ifPresent(email -> {
          if (!emailValidator.test(email)) {
            throw new ApiRequestException(email + " is not valid");
          }
          boolean taken = dataAccessService.isEmailTakenBySomeoneElse(person.getId(), email);
          if (!taken) {
            dataAccessService.updateEmail(id, email);
          }
        });

    // Check password hash
    Optional.ofNullable(person.getPasswordHash())
        .filter(password -> !StringUtils.isEmpty(password))
        .ifPresent(passwordHash ->
            dataAccessService.updatePasswordHash(id, passwordEncoder.encode(passwordHash)
    ));
  }

  public void updateLastLogin(UUID id, Timestamp lastLogin) {
    Optional.ofNullable(lastLogin)
      .ifPresentOrElse(date ->
        dataAccessService.updateLastLogin(id, date),
        () -> dataAccessService.updateLastLogin(id, new Timestamp(System.currentTimeMillis()))
      );
  }

  public int deleteByEmail(String email) {
    UUID id = dataAccessService.findIdByEmail(email);
    return dataAccessService.delete(id);
  }

}
