package com.mmanchola.blog.service;

import static org.junit.Assert.assertEquals;

import com.mmanchola.blog.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {

  @Autowired
  private PersonService personService;

  @Test
  public void canPerformCrudWhenValidUser() {
    // Create
    Person person = new Person();
    person.setFirstName("Miguel");
    person.setLastName("Manchola");
    person.setEmail("midasama3124@gmail.com");
    person.setGender("MALE");
    person.setAge((short)24);
    person.setPasswordHash("123");
    int rowsAffected = personService.add(person);
    assertEquals(1, rowsAffected);

    // Read
    Person personRetrieved = personService.getByEmail(person.getEmail()).orElse(null);
    assert personRetrieved.equals(personRetrieved);

    // Update
    String prevEmail = person.getEmail();
    person.setFirstName("Angela");
    person.setLastName("Sanchez");
    person.setEmail("angela-sanchez@hotmail.com");
    person.setGender("OTHER");
    person.setAge((short)32);
    person.setPasswordHash("345");
    personService.update(prevEmail, person);
    personRetrieved = personService.getByEmail(person.getEmail()).orElse(null);
    assert person.equals(personRetrieved);

    // Delete
    rowsAffected = personService.delete(person.getEmail());
    assertEquals(1, rowsAffected);
  }

}