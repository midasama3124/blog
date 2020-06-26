package com.mmanchola.blog.api;

import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.service.PersonService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/person")
public class PersonController {
  private PersonService personService;

  @Autowired
  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @PostMapping
  public void addPerson(@RequestBody @Validated Person person) {
    personService.add(person);
  }

  @GetMapping
  public List<Person> getAllPeople() { return personService.getAllPeople(); }

  @GetMapping(path = "{email}")
  public Person getPersonByEmail(@PathVariable("email") String email) {
    return personService.getByEmail(email)
        .orElse(null);
  }

  @PutMapping(path = "{email}")
  public void updatePersonById(@PathVariable("email") String email,
      @RequestBody @Validated Person person) {
    personService.update(email, person);
  }

  @DeleteMapping(path = "{email}")
  public void deletePerson(@PathVariable("email") String email) {
    personService.delete(email);
  }
}
