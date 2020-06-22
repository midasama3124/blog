package com.mmanchola.blog.controller;

import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {
  private PersonService personService;

  @Autowired
  public MemberController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping("/login")
  public String showLoginForm() {
    return "login-form";
  }

  @GetMapping("/register")
  public String showRegisterForm() {
    return "register-form";
  }

  @PostMapping("/register")
  public String registerMember(@Validated Person person){
    personService.addNewPerson(person);
    return "login-form";
  }

}
