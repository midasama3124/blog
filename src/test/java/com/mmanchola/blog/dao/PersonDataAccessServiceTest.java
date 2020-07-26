package com.mmanchola.blog.dao;

import com.mmanchola.blog.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonDataAccessServiceTest {

    @Autowired
    private PersonDataAccessService dataAccessService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void canPerformCRUDWithValidEntries() {
        String email = "test@gmail.com";
        String username = email.substring(0, email.indexOf('@'));
        String password = passwordEncoder.encode("123");
        UUID id = UUID.randomUUID();
        Person person = new Person(
                id,
                "Random",
                "User",
                "FEMALE",
                (short) 21,
                email,
                username,
                password,
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis())
        );

        // Create
        int rowsAffected = dataAccessService.save(person);
        assertEquals(1, rowsAffected);   // Must have added a new row

        // Read
        Person personRetrieved = dataAccessService.find(email).orElse(null);
        assert personRetrieved.equals(person);

        // Update
        UUID databaseId = dataAccessService.findIdByEmail(email).orElse(null);
        Person newPerson = new Person(
                id,
                "Freaky",
                "User",
                "MALE",
                (short) 88,
                "freky_user@yahoo.com",
                username, "adminpsw",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis())
        );
        dataAccessService.updateFirstName(databaseId, newPerson.getFirstName());
        dataAccessService.updateLastName(databaseId, newPerson.getLastName());
        dataAccessService.updateGender(databaseId, newPerson.getGender());
        dataAccessService.updateEmail(databaseId, newPerson.getEmail());
        dataAccessService.updateAge(databaseId, newPerson.getAge());
        dataAccessService.updatePasswordHash(databaseId, passwordEncoder.encode(newPerson.getPasswordHash()));
        dataAccessService.updateLastLogin(databaseId, newPerson.getLastLogin());
        Person personUpdated = dataAccessService.find(newPerson.getEmail()).orElse(null);
        assert personUpdated.equals(newPerson);

        // Delete
        id = dataAccessService.findIdByEmail(newPerson.getEmail()).orElse(null);
        int deleteRes = dataAccessService.delete(id);
        assertEquals(1, deleteRes);
    }

}