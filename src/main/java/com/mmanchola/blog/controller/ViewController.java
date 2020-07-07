package com.mmanchola.blog.controller;

import static com.mmanchola.blog.config.security.ApplicationUserRole.READER;

import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ViewController {
  private PersonService personService;

  @Autowired
  public ViewController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping(value = {"home", ""})
  public String showHome() {
    return "index";
  }

  @GetMapping("login")
  public String showLoginForm() {
    return "login-form";
  }

  @GetMapping("register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("person", new Person());
    return "register-form";
  }

  @PostMapping("register")
  public String addNewUser(Person person, Model model) {
    model.addAttribute("person", person);
    personService.add(person);
    personService.addRole(person.getEmail(), READER.name());
    return "index";
  }

  @GetMapping("post")
  public String displayPost() {
    return "post";
  }

  @GetMapping("admin")
  public String displayAdminPage() { return "admin"; }
}
