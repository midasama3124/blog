package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.*;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.util.EmailValidator;
import com.mmanchola.blog.util.FieldChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.mmanchola.blog.config.security.ApplicationUserRole.ADMIN;
import static com.mmanchola.blog.exception.ExceptionMessage.*;
import static com.mmanchola.blog.model.PersonGender.OTHER;
import static com.mmanchola.blog.model.TableFields.*;

@Service
public class PersonService {
    private final PersonDataAccessService personDas;
    private final EmailValidator emailValidator;
    private final PasswordEncoder passwordEncoder;
    private final RoleDataAccessService roleDas;
    private final PersonRoleDataAccessService personRoleDas;
    private final PostDataAccessService postDas;
    private final CommentDataAccessService commentDas;
    private final FieldChecker checker;

    @Autowired
    public PersonService(PersonDataAccessService personDas,
                         EmailValidator emailValidator,
                         PasswordEncoder passwordEncoder, RoleDataAccessService roleDas,
                         PersonRoleDataAccessService personRoleDas, PostDataAccessService postDas, CommentDataAccessService commentDas, FieldChecker checker) {
        this.personDas = personDas;
        this.emailValidator = emailValidator;
        this.passwordEncoder = passwordEncoder;
        this.roleDas = roleDas;
        this.personRoleDas = personRoleDas;
        this.postDas = postDas;
        this.commentDas = commentDas;
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
                .ifPresentOrElse(
                        person::setGender,
                        () -> person.setGender(OTHER.toString())
                );
        // Check email
        String email = checkEmailCorrectness(person.getEmail())
                .orElseThrow(() -> new ApiRequestException(MISSING_INVALID.getMsg(PERSON_EMAIL.toString())));
        checkEmailAvailability(email)
                .orElseThrow(() -> new ApiRequestException(UNAVAILABLE.getMsg(PERSON_EMAIL.toString())));
        // Set username
        String username = email.substring(0, email.indexOf('@'));
        person.setUsername(username);
        // Check password hash
        checker.checkNotEmpty(person.getPasswordHash())
                .ifPresentOrElse(password ->
                                // Encrypt password
                                person.setPasswordHash(passwordEncoder.encode(password)
                                ),
                        () -> {
                            throw new ApiRequestException(MISSING.getMsg(PERSON_PASSWORD.toString()));
                        }
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

    // Remove role to given user by his/her email
    public int removeRole(String userEmail, String roleName) {
        UUID userId = personDas.findIdByEmail(userEmail)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        short roleId = roleDas.findIdByName(roleName)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(ROLE_NAME.toString())));
        return personRoleDas.deleteSingleEntry(userId, roleId);
    }

    // Check if user is admin
    public boolean isAdmin(String email) {
        UUID personId = personDas.findIdByEmail(email)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        short roleId = roleDas.findIdByName(ADMIN.toString()).get();
        return personRoleDas.hasRole(personId, roleId);
    }

    // Get all people on database
    public List<Person> getAll() {
        return personDas.findAll();
    }

    // Get all people with admin privileges
    public List<Person> getAdmins() {
        short adminRoleId = roleDas.findIdByName(ADMIN.name())
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(ROLE_NAME.toString())));
        List<UUID> adminPersonIds = personRoleDas.findPeople(adminRoleId);
        List<Person> admins = new ArrayList<>();
        for (UUID personId : adminPersonIds) {
            Person admin = personDas.find(personId)
                    .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_ID.toString())));
            admins.add(admin);
        }
        return admins;
    }

    // Get person by his/her email
    public Optional<Person> get(String email) {
        return personDas.find(email);
    }

    // Get person by his/her email
    public Optional<Person> get(UUID id) {
        return personDas.find(id);
    }

    // Update member email
    private void updateEmail(UUID memberId, String newEmail) {
        String updateEmail = checkEmailCorrectness(newEmail)
                .orElseThrow(() -> new ApiRequestException(MISSING_INVALID.getMsg(PERSON_EMAIL.toString())));
        checkEmailAvailability(updateEmail, memberId)
                .ifPresentOrElse(
                        em -> personDas.updateEmail(memberId, em),
                        () -> {
                            throw new ApiRequestException(UNAVAILABLE.getMsg(PERSON_EMAIL.toString()));
                        }
                );
    }

    // Update member password
    private void updatePassword(UUID memberId, String password) {
        checker.checkNotEmpty(password)
                .ifPresent(
                        // Encode password
                        passwordHash ->
                                personDas.updatePasswordHash(memberId, passwordEncoder.encode(passwordHash))
                );
    }

    public void update(String email, Person person) {
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
        // Change password
        updatePassword(id, person.getPasswordHash());
        // Change email and username if different
        if (!email.equals(person.getEmail())) {
            String newEmail = person.getEmail();
            updateEmail(id, newEmail);
            String newUsername = email.substring(0, email.indexOf('@'));
            personDas.updateUsername(id, newUsername);
        }
    }

    public void updateLastLogin(String email) {
        UUID id = personDas.findIdByEmail(email)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        personDas.updateLastLogin(id, new Timestamp(System.currentTimeMillis()));
    }

    public int deleteByEmail(String email) {
        UUID id = personDas.findIdByEmail(email)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));
        // First delete all roles
        personRoleDas.deletePerson(id);
        return personDas.delete(id);
    }

}
