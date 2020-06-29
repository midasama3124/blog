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
    person.setFirstName("James");
    person.setLastName("Bond");
    person.setEmail("james-bond@gmail.com");
    person.setGender("MALE");
    person.setAge((short)45);
    person.setPasswordHash("123");
    int rowsAffected = personService.add(person);
    assertEquals(1, rowsAffected);

    // Read
    Person personRetrieved = personService.getByEmail(person.getEmail()).orElse(null);
    assert personRetrieved.equals(personRetrieved);

    // Update
    String prevEmail = person.getEmail();
    person.setFirstName("Katharina");
    person.setLastName("Nielsen");
    person.setEmail("katha_nielsen@hotmail.com");
    person.setGender("OTHER");
    person.setAge((short)38);
    person.setPasswordHash("345");
    personService.update(prevEmail, person);
    personRetrieved = personService.getByEmail(person.getEmail()).orElse(null);
    assert person.equals(personRetrieved);

    // Delete
    rowsAffected = personService.deleteByEmail(person.getEmail());
    assertEquals(1, rowsAffected);
  }

}