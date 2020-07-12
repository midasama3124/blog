package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
  public String addNewUser(Person person, Model model, HttpServletRequest request) {
    model.addAttribute("person", person);
    String psw = person.getPasswordHash();
    // TODO: Fix bug when registering with only email and psw given
    personService.add(person);
//    personService.addRole(person.getEmail(), READER.name());
    // Auto login after successful registration
    try {
      request.login(person.getEmail(), psw);
    } catch (ServletException e) {
      throw new ApiRequestException("Error while login after registration: " + e);
    }
    return "index";
  }

  @GetMapping("post")
  public String displayPost() {
    return "post";
  }
}
