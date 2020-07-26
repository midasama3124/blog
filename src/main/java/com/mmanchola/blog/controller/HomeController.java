package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.*;
import com.mmanchola.blog.service.CommentService;
import com.mmanchola.blog.service.PersonService;
import com.mmanchola.blog.service.PostService;
import com.mmanchola.blog.service.TagService;
import com.mmanchola.blog.util.MarkdownParser;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private CommentService commentService;
    private PrettyTime prettyTime;

    @Autowired
    public HomeController(PersonService personService, PostService postService, TagService tagService, CommentService commentService) {
        this.personService = personService;
        this.postService = postService;
        this.tagService = tagService;
        this.commentService = commentService;
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
                .flatMap(personService::get)
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
        return "redirect:/home";
    }

    @GetMapping("update/{id}")
    public String showUpdateUserForm(@PathVariable("id") UUID id,
                                     @ModelAttribute("genderMap") Map<String, String> genderMap,
                                     Model model) {
        Person person = personService.get(id).get();
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
            String email = personService.get(id).get().getEmail();
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
                              @PathVariable("slug") String slug,
                              RedirectAttributes redirectAttributes) {
        model.addAttribute("person", person);
        Post post;
        try {
            post = postService.getBySlug(slug);
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "error";
        }
        if (post.getStatus().equals(PUBLISHED.toString())) {
            post.setContent(MarkdownParser.parse(post.getContent()));
            model.addAttribute("status", "published");
            model.addAttribute("post", post);
            // Retrieve author name
            Person author = personService.get(post.getPersonId()).get();
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
            /* Post comments */
            List<Comment> comments = postService.getComments(post.getSlug());
            HashMap<UUID, String> authorNameMap = new HashMap<>();
            HashMap<Long, String> commentPubMap = new HashMap<>();
            for (Comment c : comments) {
                // Reader names
                Person reader = personService.get(c.getPersonId()).get();
                String readerName = reader.getFirstName().isEmpty() ?
                        reader.getUsername() :
                        reader.getFirstName() + " " + reader.getLastName();
                authorNameMap.put(reader.getId(), readerName);
                // Transformation of publication timestamp
                commentPubMap.put(c.getId(), prettyTime.format(c.getPublishedAt()));
            }
            model.addAttribute("comment", new Comment());
            model.addAttribute("comments", comments);
            model.addAttribute("authorNameMap", authorNameMap);
            model.addAttribute("commentPubMap", commentPubMap);
            // Post likes
            boolean isAlreadyLiked = person != null ? postService.isAlreadyLiked(slug, person.getEmail()) : false;
            model.addAttribute("isAlreadyLiked", isAlreadyLiked);
            model.addAttribute("numLikes", postService.getLikes(slug));
            return "post";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "No pudo encontrarse esta página");
        return "error";   // TODO: Create html page for error prompts
    }

    @PostMapping("comment/new/{slug}")
    @PreAuthorize("hasAnyRole('ROLE_READER')")
    public String commentPost(@PathVariable("slug") String slug,
                              @ModelAttribute("member") Person person,
                              RedirectAttributes redirectAttributes,
                              Comment comment,
                              Model model) {
        model.addAttribute("comment", comment);
        try {
            Post post = postService.getBySlug(slug);
            postService.addComment(comment, post.getId(), person.getEmail());
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return String.format("redirect:/post/%s#comment-content", slug);
        }
        return String.format("redirect:/post/%s#comment-list", slug);
    }

    @PostMapping("comment/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String deleteComment(@PathVariable("id") long commentId,
                                RedirectAttributes redirectAttributes) {
        String postSlug = new String();
        try {
            Comment comment = commentService.get(commentId);
            commentService.delete(comment.getId());
            postSlug = postService.getSlugById(comment.getPostId());
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "error";
        }
        return String.format("redirect:/post/%s#comment-list", postSlug);
    }

    @PostMapping("like/new/{slug}")
    @PreAuthorize("hasAnyRole('ROLE_READER')")
    public String likePost(@PathVariable("slug") String slug,
                           @ModelAttribute("member") Person person,
                           RedirectAttributes redirectAttributes,
                           Like like,
                           Model model) {
        model.addAttribute("like", like);
        try {
            Post post = postService.getBySlug(slug);
            postService.addLike(like, post.getId(), person.getEmail());
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "error";
        }
        return String.format("redirect:/post/%s#post-interaction", slug);
    }

}
