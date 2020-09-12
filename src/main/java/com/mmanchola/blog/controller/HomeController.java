package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.*;
import com.mmanchola.blog.service.*;
import com.mmanchola.blog.util.MarkdownParser;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.mmanchola.blog.config.security.ApplicationUserRole.READER;
import static com.mmanchola.blog.model.PostStatus.PUBLISHED;

@Controller
@RequestMapping("/")
public class HomeController {
    private final PersonService personService;
    private final PostService postService;
    private final TagService tagService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final PrettyTime prettyTime;
    private final JavaMailSender mailSender;
    private final MessageSource messages;
    private final Environment env;

    @Autowired
    public HomeController(PersonService personService, PostService postService,
                          TagService tagService, CommentService commentService,
                          CategoryService categoryService, ImageService imageService,
                          JavaMailSender mailSender,
                          @Qualifier("messageSource") MessageSource messages,
                          Environment env) {
        this.personService = personService;
        this.postService = postService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.mailSender = mailSender;
        this.messages = messages;
        this.env = env;
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
                .map(personService::get)
                .orElse(null);
    }

    @GetMapping(value = {"home", ""})
    public String showHome(@ModelAttribute("member") Person person, Model model) {
        model.addAttribute("person", person);
        // Get recent posts
        List<Post> mostRecent = postService.getMostRecent(6);
        model.addAttribute("mostRecent", mostRecent);
        // Get moments ago
        model.addAttribute("momentsAgo", getMomentsAgoMap(mostRecent));
        // Get parent categories
        model.addAttribute("categories", categoryService.getParents());
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
        Person person = personService.get(id);
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
            String email = personService.get(id).getEmail();
            personService.update(email, person);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register-form";
        }
        // Change authentication credentials after successful update of username/password
        UsernamePasswordAuthenticationToken renewedAuth = new UsernamePasswordAuthenticationToken(username,
                password);
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
        model.addAttribute("categories", categoryService.getParents());
        Post post;
        try {
            post = postService.getBySlug(slug);
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
        if (post.getStatus().equals(PUBLISHED.toString())) {
            post.setContent(MarkdownParser.parse(post.getContent()));
            model.addAttribute("status", "published");
            model.addAttribute("post", post);
            // Retrieve author name
            model.addAttribute("author", personService.get(post.getPersonId()));
            // Compute moment ago in Spanish
            model.addAttribute("momentsAgo", prettyTime.format(post.getPublishedAt()));
            // Compute estimated reading time based on content length
            model.addAttribute("readTime",
                    post.getContent().split("\\W+").length / 200);
            // Post tags
            List<Tag> tags = postService.getTags(post.getSlug())
                    .stream()
                    .map(tagService::get)
                    .collect(Collectors.toList());
            model.addAttribute("tags", tags);
            /* Post comments */
            List<Comment> comments = postService.getComments(post.getSlug());
            HashMap<UUID, String> authorNameMap = new HashMap<>();
            HashMap<Long, String> commentPubMap = new HashMap<>();
            for (Comment c : comments) {
                // Reader names
                Person reader = personService.get(c.getPersonId());
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
            boolean isAlreadyLiked = person != null && postService.isAlreadyLiked(slug, person.getEmail());
            model.addAttribute("isAlreadyLiked", isAlreadyLiked);
            model.addAttribute("numLikes", postService.getLikes(slug));
            // Popular posts for sidebar
            model.addAttribute("popularPosts", postService.getPopular(3));
            // Popular tags for sidebar
            model.addAttribute("popularTags", tagService.getPopular(5));
            return "post";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "No pudo encontrarse esta página");
        return "redirect:/error";
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
        String postSlug;
        try {
            Comment comment = commentService.get(commentId);
            commentService.delete(comment.getId());
            postSlug = postService.getSlugById(comment.getPostId());
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
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
            return "redirect:/error";
        }
        return String.format("redirect:/post/%s#post-interaction", slug);
    }

    @GetMapping("category/{slug}")
    public String displayCategoryPage(@PathVariable String slug,
                                      Model model,
                                      RedirectAttributes redirectAttributes,
                                      @ModelAttribute("member") Person person) {
        model.addAttribute("person", person);
        model.addAttribute("categories", categoryService.getParents());
        try {
            // Get category
            Category category = categoryService.getBySlug(slug);
            model.addAttribute("ctag", category);
            // Get posts
            List<Post> posts = postService.getByCategory(slug);
            model.addAttribute("posts", posts);
            // Get moments ago
            model.addAttribute("momentsAgo", getMomentsAgoMap(posts));
            // Get children
            model.addAttribute("children", categoryService.getChildren(slug));
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.toString());
            return "redirect:/error";
        }
        return "category-tag";
    }

    @GetMapping("tag/{slug}")
    public String displayTagPage(@PathVariable String slug,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 @ModelAttribute("member") Person person) {
        model.addAttribute("person", person);
        model.addAttribute("categories", categoryService.getParents());
        try {
            // Get tag
            Tag tag = tagService.getBySlug(slug);
            model.addAttribute("ctag", tag);
            // Get posts
            List<Post> posts = postService.getByTag(slug);
            model.addAttribute("posts", posts);
            // Get moments ago
            model.addAttribute("momentsAgo", getMomentsAgoMap(posts));
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.toString());
            return "redirect:/error";
        }
        return "category-tag";
    }

    @GetMapping("error")
    public String displayErrorPage(HttpServletRequest request,
                                   Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorStatus", "404 Not Found");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorStatus", "500 Internal Server Error");
            }
        }
        return "error";
    }

    @GetMapping("recover")
    public String displayRecoverPwdForm(Model model) {
        model.addAttribute("emailContainer", new EmailContainer());
        return "recover-password";
    }

    @PostMapping("recover")
    public String recoverPassword(EmailContainer emailContainer,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  final HttpServletRequest request) {
        Person person;
        try {
            String email = emailContainer.getEmail();
            person = personService.get(email);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", "El email no se encuentra registrado");
            return "recover-password";
        }
        mailSender.send(constructResetEmail(getAppUrl(request),
                new Locale("es", "ES"), person));
        redirectAttributes.addFlashAttribute("message", "Un link de recuperación ha sido enviado a su correo");
        return "redirect:/recover";
    }

    @GetMapping("contact")
    public String displayContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact-form";
    }

    @PostMapping("contact")
    public String sendContactForm(ContactForm contactForm,
                                  RedirectAttributes redirectAttributes,
                                  @ModelAttribute("member") Person person) {
        if (person != null) {
            if (!person.getFirstName().isEmpty())
                contactForm.setName(person.getFirstName() + " " + person.getLastName());
            contactForm.setEmail(person.getEmail());
        }
        try {
            for (Person admin : personService.getAdmins())
                mailSender.send(constructContactEmail(
                        new Locale("es", "ES"), contactForm, admin)
                );
            redirectAttributes.addFlashAttribute("message", "Tu mensaje fue enviado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Algo ha salido mal. Lo sentimos.");
        }
        return "redirect:/contact";
    }

    @GetMapping(
            value = "image/download/{key}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] downloadImage(@PathVariable("key") String key, Model model,
                         HttpServletResponse response) {
        return imageService.downloadImage(key);
    }

    // ============== NON-API ============

    // Get map with moments ago
    private Map<Integer, String> getMomentsAgoMap(List<Post> posts) {
        Map<Integer, String> momentsAgo = new HashMap<>();
        for (Post p : posts) {
            momentsAgo.put(p.getId(), prettyTime.format(p.getPublishedAt()));
        }
        return momentsAgo;
    }

    private SimpleMailMessage constructResetEmail(
            String contextPath, Locale locale, Person person) {
        String url = contextPath + "/update/" + person.getId().toString();
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail("Recuperación de contraseña - Con Bellas Palabras", message + " \r\n" + url, person.getEmail());
    }

    private SimpleMailMessage constructContactEmail(Locale locale, ContactForm contactForm, Person person) {
        String subject = "Nuevo mensaje - " + contactForm.getSubject();
        String message = messages.getMessage("message.newContactForm", null, locale) +
                "\n\n" + contactForm.toString();
        return constructEmail(subject, message, person.getEmail());
    }

    private SimpleMailMessage constructEmail(String subject, String body, String emailAddress) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(emailAddress);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
