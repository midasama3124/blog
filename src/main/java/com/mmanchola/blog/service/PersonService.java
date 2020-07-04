package com.mmanchola.blog.service;

import static com.mmanchola.blog.exception.ExceptionMessage.MISSING;
import static com.mmanchola.blog.exception.ExceptionMessage.MISSING_INVALID;
import static com.mmanchola.blog.exception.ExceptionMessage.NOT_FOUND;
import static com.mmanchola.blog.exception.ExceptionMessage.UNAVAILABLE;
import static com.mmanchola.blog.model.TableFields.PERSON_EMAIL;
import static com.mmanchola.blog.model.TableFields.PERSON_PASSWORD;
import static com.mmanchola.blog.model.TableFields.ROLE_NAME;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.dao.PersonRoleDataAccessService;
import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.util.EmailValidator;
import com.mmanchola.blog.util.ServiceChecker;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
  private final PersonDataAccessService personDas;
  private final EmailValidator emailValidator;
  private final PasswordEncoder passwordEncoder;
  private final RoleDataAccessService roleDas;
  private final PersonRoleDataAccessService personRoleDas;
  private final ServiceChecker checker;

  @Autowired
  public PersonService(PersonDataAccessService personDas,
      EmailValidator emailValidator,
      PasswordEncoder passwordEncoder, RoleDataAccessService roleDas,
      PersonRoleDataAccessService personRoleDas, ServiceChecker checker) {
    this.personDas = personDas;
    this.emailValidator = emailValidator;
    this.passwordEncoder = passwordEncoder;
    this.roleDas = roleDas;
    this.personRoleDas = personRoleDas;
    this.checker = checker;
  }

  // Check given person email in terms of correctness
  private Optional<String> checkEmailCorrectness(String email) {
    return Optional.ofNullable(email)
        .filter(emailValidator::test);
  }

  // Check given person email in terms of availability for POST operations
  private Optional<String> checkEmailAvailability(String email) {
    return Optional.ofNullable(email)
        .filter(Predicate.not(personDas::isEmailTaken));
  }

  // Check given person email in terms of availability for PUT operations
  private Optional<String> checkEmailAvailability(String email, UUID exclusiveId) {
    return Optional.ofNullable(email)
        .filter(e -> !personDas.isEmailTakenBySomeoneElse(exclusiveId, e));
  }

  // Add new person to database
  public int add(Person person) {
    // Check first name
    checker.checkPersonName(person.getFirstName())
        .ifPresent(person::setFirstName);
    // Check last name
    checker.checkPersonName(person.getLastName())
        .ifPresent(person::setLastName);
    // Check gender
    checker.checkGender(person.getGender())
        .ifPresent(person::setGender);
    // Check email
    String email = checkEmailCorrectness(person.getEmail())
        .orElseThrow(() -> new ApiRequestException(MISSING_INVALID.getMsg(PERSON_EMAIL.toString())));
    checkEmailAvailability(email)
        .orElseThrow(() -> new ApiRequestException(UNAVAILABLE.getMsg(PERSON_EMAIL.toString())));
    // Check password hash
    checker.checkNotEmpty(person.getPasswordHash())
        .ifPresentOrElse(password ->
            // Encrypt password
            person.setPasswordHash(passwordEncoder.encode(password)
        ),
            () -> { throw new ApiRequestException(MISSING.getMsg(PERSON_PASSWORD.toString())); }
        );
    return personDas.save(person);
  }

  // Add role to given user by his/her email
  public int addRole(String userEmail, String roleName) {
    UUID userId = personDas.findIdByEmail(userEmail)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    short roleId = roleDas.findIdByName(roleName)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(ROLE_NAME.toString())));
    return personRoleDas.save(userId, roleId);
  }

  // Get all people on database
  public List<Person> getAll() { return personDas.findAll(); }

  // Get person by his/her email
  public Optional<Person> getByEmail(String email) {
    return personDas.findByEmail(email);
  }

  // Update member email
  public void updateEmail(String newEmail, String prevEmail) {
    UUID memberId = personDas.findIdByEmail(prevEmail)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    String updateEmail = checkEmailCorrectness(newEmail)
        .orElseThrow(() -> new ApiRequestException(MISSING_INVALID.getMsg(PERSON_EMAIL.toString())));
    checkEmailAvailability(updateEmail, memberId)
        .ifPresentOrElse(
            em -> personDas.updateEmail(memberId, em),
            () -> { throw new ApiRequestException(UNAVAILABLE.getMsg(PERSON_EMAIL.toString())); }
        );
  }

  // Update member password
  public void updatePassword(String password, String email) {
    UUID memberId = personDas.findIdByEmail(email)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    checker.checkNotEmpty(password)
        .ifPresentOrElse(
            // Encode password
            passwordHash ->
                personDas.updatePasswordHash(memberId, passwordEncoder.encode(passwordHash)),
            () -> { throw new ApiRequestException(MISSING.getMsg(PERSON_PASSWORD.toString())); }
        );
  }

  public void updatePersonalInfo(String email, Person person) {
    // Retrieve id from database
    UUID id = personDas.findIdByEmail(email)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    // Check first name
    checker.checkPersonName(person.getFirstName())
        .ifPresent(firstName -> personDas.updateFirstName(id, firstName));
    // Check last name
    checker.checkPersonName(person.getLastName())
        .ifPresent(lastName -> personDas.updateLastName(id, lastName));
    // Check gender
    checker.checkGender(person.getGender())
        .ifPresent(gender -> personDas.updateGender(id, gender));
    // Check age
    checker.checkAge(person.getAge())
        .ifPresent(age -> personDas.updateAge(id, age));
  }

  public void updateLastLogin(String email) {
    UUID id = personDas.findIdByEmail(email)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    personDas.updateLastLogin(id, new Timestamp(System.currentTimeMillis()));
  }

  public int deleteByEmail(String email) {
    UUID id = personDas.findIdByEmail(email)
        .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
    return personDas.delete(id);
  }

}
