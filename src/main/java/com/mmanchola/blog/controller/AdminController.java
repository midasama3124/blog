package com.mmanchola.blog.controller;

import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final PersonService personService;

    @Autowired
    public AdminController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String displayAdminPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        Person user = personService.getByEmail(username).get();
        model.addAttribute("member", user);
        model.addAttribute("admins", personService.getAdmins());
        return "admin";
    }

}
