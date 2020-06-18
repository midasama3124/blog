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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonService {
  private final PersonDataAccessService dataAccessService;
  private final EmailValidator emailValidator;

  @Autowired
  public PersonService(PersonDataAccessService dataAccessService,
      EmailValidator emailValidator) {
    this.dataAccessService = dataAccessService;
    this.emailValidator = emailValidator;
  }

  public int addNewPerson(Person person) { return addNewPerson(null, person); }

  public int addNewPerson(UUID id, Person person) {
    UUID newId = Optional.ofNullable(id)
        .orElse(UUID.randomUUID());

    // Check Email
    if (!emailValidator.test(person.getEmail())) {
      throw new ApiRequestException(person.getEmail() + " is not valid");
    }

    if (dataAccessService.isEmailTaken(person.getEmail())) {
      throw new ApiRequestException(person.getEmail() + " is already taken");
    }

    // Check username
    if (dataAccessService.isUsernameTaken(person.getUsername())) {
      throw new ApiRequestException(person.getUsername() + " is already taken");
    }

    return dataAccessService.create(newId, person);
  }

  public List<Person> getAllPeople() { return dataAccessService.readAll(); }

  public Optional<Person> getPersonById(UUID id) { return dataAccessService.readById(id); }

  public void updatePerson(UUID id, Person person) {
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
    Optional.ofNullable(person.getUsername())
        .filter(username -> !StringUtils.isEmpty(username))
        .map(String::toLowerCase)
        .ifPresent(username -> {
          username = username.replaceAll("\\s", "");  // Remove white spaces from username
          if (dataAccessService.isUsernameTakenBySomeoneElse(id, username)) {
            throw new ApiRequestException(username + " is already taken");
          }
          dataAccessService.updateUsername(id, username);
        });

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
    // TODO: Hash given password to save an encryption into the database
    Optional.ofNullable(person.getPasswordHash())
        .filter(password -> !StringUtils.isEmpty(password))
        .ifPresent(passwordHash -> dataAccessService.updatePasswordHash(id, passwordHash));
  }

  public void updateLastLogin(UUID id, Timestamp lastLogin) {
    Optional.ofNullable(lastLogin)
      .ifPresentOrElse(date ->
        dataAccessService.updateLastLogin(id, date),
        () -> dataAccessService.updateLastLogin(id, new Timestamp(System.currentTimeMillis()))
      );
  }

  public int deletePersonById(UUID id) { return dataAccessService.deleteById(id); }
}
