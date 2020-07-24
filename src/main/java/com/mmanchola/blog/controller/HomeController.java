package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.model.PersonGender;
import com.mmanchola.blog.model.Post;
import com.mmanchola.blog.model.Tag;
import com.mmanchola.blog.service.PersonService;
import com.mmanchola.blog.service.PostService;
import com.mmanchola.blog.service.TagService;
import com.mmanchola.blog.util.MarkdownParser;
import org.ocpsoft.prettytime.PrettyTime;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.mmanchola.blog.config.security.ApplicationUserRole.READER;
import static com.mmanchola.blog.model.PostStatus.PUBLISHED;

@Controller
@RequestMapping("/")
public class HomeController {
    private PersonService personService;
    private PostService postService;
    private TagService tagService;
    private PrettyTime prettyTime;

    @Autowired
    public HomeController(PersonService personService, PostService postService, TagService tagService) {
        this.personService = personService;
        this.postService = postService;
        this.tagService = tagService;
        this.prettyTime = new PrettyTime(new Locale("es"));
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
        List<Post> mostRecent = postService.getMostRecent(6);
        model.addAttribute("mostRecent", mostRecent);
        Map<Integer, String> momentsAgo = new HashMap<>();
        for (Post p : mostRecent) {
            momentsAgo.put(p.getId(), prettyTime.format(p.getPublishedAt()));
        }
        model.addAttribute("momentsAgo", momentsAgo);
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

    @GetMapping("post/{slug}")
    public String displayPost(@ModelAttribute("member") Person person,
                              Model model,
                              @PathVariable("slug") String slug) {
        model.addAttribute("person", person);
        Post post = postService.getBySlug(slug).orElse(null);
        if (post != null && post.getStatus().equals(PUBLISHED.toString())) {
            post.setContent(MarkdownParser.parse(post.getContent()));
            model.addAttribute("status", "published");
            model.addAttribute("post", post);
            // Retrieve author name
            Person author = personService.getById(post.getPersonId()).get();
            model.addAttribute("author", author);
            // Compute moment ago in Spanish
            String momentsAgo = prettyTime.format(post.getPublishedAt());
            model.addAttribute("momentsAgo", momentsAgo);
            // Compute estimated reading time based on content length
            int readTime = post.getContent().split("\\W+").length / 200;
            model.addAttribute("readTime", readTime);
            // Post tags
            List<Tag> tags = postService.getTags(post.getSlug())
                    .stream()
                    .map(id -> tagService.get(id).get())
                    .collect(Collectors.toList());
            model.addAttribute("tags", tags);
            // TODO: Retrieve comments
            return "post";
        }
        return "error";   // TODO: Create html page for error prompts
    }
}
