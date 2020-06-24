package com.mmanchola.blog.controller;

import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {
  private PersonService personService;

  @Autowired
  public RegistrationController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping
  public String showRegistrationForm(Model model) {
    model.addAttribute("person", new Person());
    return "register-form";
  }

  @PostMapping
  public String addNewUser(Person person, Model model) {
    model.addAttribute("person", person);
    personService.addNewPerson(person);
    return "index";
  }
}
