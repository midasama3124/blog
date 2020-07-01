package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.util.EmailValidator;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
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
        .filter(Predicate.not(String::isEmpty))
        .map(StringUtils::capitalize)
        .ifPresent(person::setFirstName);

    // Check last name
    Optional.ofNullable(person.getLastName())
        .filter(Predicate.not(String::isEmpty))
        .map(StringUtils::capitalize)
        .ifPresent(person::setLastName);

    // Check gender
    Optional.ofNullable(person.getGender())
        .filter(Predicate.not(String::isEmpty))
        .map(String::toUpperCase)
        .ifPresent(gender -> {
          if (gender.equals("MALE") || gender.equals("FEMALE"))
            person.setGender(gender);
          else
            person.setGender("OTHER");
        });

    // Check email
    Optional.ofNullable(person.getEmail())
        .filter(Predicate.not(String::isEmpty))
        .ifPresentOrElse(email -> {
          if (!emailValidator.test(email))
            throw new ApiRequestException(email + " is not valid");
          if (dataAccessService.isEmailTaken(email))
            throw new ApiRequestException(email + " is already taken");
        }, IllegalStateException::new);

    // Check password hash
    Optional.ofNullable(person.getPasswordHash())
        .filter(Predicate.not(String::isEmpty))
        .ifPresentOrElse(passwordHash ->
            // Encrypt password
            person.setPasswordHash(passwordEncoder.encode(passwordHash)
        ), IllegalStateException::new);

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
        .filter(Predicate.not(String::isEmpty))
        .map(StringUtils::capitalize)
        .ifPresent(firstName -> dataAccessService.updateFirstName(id, firstName));

    // Check last name
    Optional.ofNullable(person.getLastName())
        .filter(Predicate.not(String::isEmpty))
        .map(StringUtils::capitalize)
        .ifPresent(lastName -> dataAccessService.updateLastName(id, lastName));

    // Check gender
    Optional.ofNullable(person.getGender())
        .filter(Predicate.not(String::isEmpty))
        .map(String::toUpperCase)
        .ifPresent(gender -> {
          if (gender.equals("MALE") || gender.equals("FEMALE"))
            dataAccessService.updateGender(id, gender);
          else dataAccessService.updateGender(id, "OTHER");
        });

    // Check age
    Optional.ofNullable(person.getAge())
        .ifPresent(age -> dataAccessService.updateAge(id, age));

    // Check username
//    Optional.ofNullable(person.getUsername())
//        .filter(Predicate.not(String::isEmpty))
//        .map(String::toLowerCase)
//        .map(username -> StringUtils.replace(username, " ", "_"))
//        .ifPresent(username -> {
//          if (dataAccessService.isUsernameTakenBySomeoneElse(id, username)) {
//            throw new ApiRequestException(username + " is already taken");
//          }
//          dataAccessService.updateUsername(id, username);
//        });

    // Check email
    Optional.ofNullable(person.getEmail())
        .filter(Predicate.not(String::isEmpty))
        .ifPresent(email -> {
          if (!emailValidator.test(email)) {
            throw new ApiRequestException(email + " is not valid");
          }
          boolean taken = dataAccessService.isEmailTakenBySomeoneElse(person.getId(), email);
          if (!taken) dataAccessService.updateEmail(id, email);
        });

    // Check password hash
    Optional.ofNullable(person.getPasswordHash())
        .filter(Predicate.not(String::isEmpty))
        .ifPresent(passwordHash ->
            dataAccessService.updatePasswordHash(id, passwordEncoder.encode(passwordHash))
        );
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
