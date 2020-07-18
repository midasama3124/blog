package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.model.PersonGender;
import com.mmanchola.blog.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.mmanchola.blog.config.security.ApplicationUserRole.READER;

@Controller
@RequestMapping("/")
public class HomeController {
    private PersonService personService;

    @Autowired
    public HomeController(PersonService personService) {
        this.personService = personService;
    }

    @ModelAttribute("genderMap")
    public Map<String, String> getGenderMap() {
        Map<String, String> map = new HashMap<>();
        map.put("MALE", "Hombre");
        map.put("FEMALE", "Mujer");
        map.put("OTHER", "Otro");
        return map;
    }

    @ModelAttribute("member")
    public Person findLoggedUser(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(Authentication::getName)
                .flatMap(personService::getByEmail)
                .orElse(null);
    }

    @GetMapping(value = {"home", ""})
    public String showHome(@ModelAttribute("member") Person person, Model model) {
        model.addAttribute("person", person);
        return "index";
    }

    @GetMapping("login")
    public String showLoginForm() {
        return "login-form";
    }

    @GetMapping("register")
    public String showRegistrationForm(@ModelAttribute("genderMap") Map<String, String> genderMap, Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("genders", PersonGender.values());
        model.addAttribute("genderMap", genderMap);
        return "register-form";
    }

    @PostMapping("register")
    public String addNewUser(Person person, Model model, HttpServletRequest request) {
        model.addAttribute("person", person);
        String psw = person.getPasswordHash();
        try {
            personService.add(person);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register-form";
        }
        personService.addRole(person.getEmail(), READER.name());
        // Auto login after successful registration
        try {
            request.login(person.getEmail(), psw);
        } catch (ServletException e) {
            throw new ApiRequestException("Error while login after registration: " + e);
        }
        return "index";
    }

    @GetMapping("update/{id}")
    public String showUpdateUserForm(@PathVariable("id") UUID id,
                                     @ModelAttribute("genderMap") Map<String, String> genderMap,
                                     Model model) {
        Person person = personService.getById(id).get();
        model.addAttribute("id", id);
        model.addAttribute("person", person);
        model.addAttribute("genders", PersonGender.values());
        model.addAttribute("genderMap", genderMap);
        model.addAttribute("method", "PUT");
        return "register-form";
    }

    @PostMapping("update/{id}")
    public String updateTag(
            @PathVariable("id") UUID id,
            Person person,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication auth) {
        model.addAttribute("person", person);
        String username = person.getEmail();
        String password = person.getPasswordHash().isEmpty() ? auth.getCredentials().toString() :
                person.getPasswordHash();
        try {
            String email = personService.getById(id).get().getEmail();
            personService.update(email, person);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register-form";
        }
        // Change authentication credentials after successful update of username/password
        UsernamePasswordAuthenticationToken renewedAuth = new UsernamePasswordAuthenticationToken(username,
                password, auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(renewedAuth);
        redirectAttributes.addFlashAttribute("message", "Los datos del usuario fueron actualizado exitosamente");
        return "redirect:/home";
    }

    @GetMapping("post")
    public String displayPost() {
        return "post";
    }
}
