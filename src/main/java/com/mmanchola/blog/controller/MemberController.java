package com.mmanchola.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

  @GetMapping("/login")
  public String showLoginForm() {
    return "login-form";
  }

  @GetMapping("/register")
  public String showRegisterForm() {
    return "register-form";
  }

}
