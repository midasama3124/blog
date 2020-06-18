package com.mmanchola.blog.api;

import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.service.PersonService;
import java.util.List;
import java.util.UUID;
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
  private final PersonService personService;

  @Autowired
  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @PostMapping
  public void addPerson(@RequestBody @Validated Person person) {
    personService.addNewPerson(person);
  }

  @GetMapping
  public List<Person> getAllPeople() { return personService.getAllPeople(); }

  @GetMapping(path = "{personId}")
  public Person getPersonById(@PathVariable("personId") UUID id) {
    return personService.getPersonById(id)
        .orElse(null);
  }

  @PutMapping(path = "{personId}")
  public void updatePersonById(@PathVariable("personId") UUID id,
      @RequestBody @Validated Person person) {
    personService.updatePerson(id, person);
  }

  @DeleteMapping(path = "{personId}")
  public void deletePerson(@PathVariable("personId") UUID id) {
    personService.deletePersonById(id);
  }
}
