package com.mmanchola.blog.service;

import static com.mmanchola.blog.security.ApplicationUserRole.ADMIN;
import static com.mmanchola.blog.security.ApplicationUserRole.READER;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.util.EmailValidator;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonService implements UserDetailsService {
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

  public int addNewPerson(Person person) {

    // Check Email
    if (!emailValidator.test(person.getEmail())) {
      throw new ApiRequestException(person.getEmail() + " is not valid");
    }

    if (dataAccessService.isEmailTaken(person.getEmail())) {
      throw new ApiRequestException(person.getEmail() + " is already taken");
    }

    return dataAccessService.save(person);
  }

  public List<Person> getAllPeople() { return dataAccessService.findAll(); }

  public Optional<Person> getPersonByEmail(String email) { return dataAccessService.findByEmail(email); }

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

  public int deletePersonById(UUID id) { return dataAccessService.deleteById(id); }

  /* Security-related methods */
  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    Person person = dataAccessService.findByEmail(s).get();

    // TODO: Retrieve corresponding roles from database
    List<GrantedAuthority> roles = new ArrayList<>();
    roles.add(new SimpleGrantedAuthority(ADMIN.name()));
    roles.add(new SimpleGrantedAuthority(READER.name()));

    UserDetails userDetails = new User(person.getEmail(), person.getPasswordHash(), roles);

    return userDetails;
  }
}
