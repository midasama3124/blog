package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.*;
import com.mmanchola.blog.service.*;
import com.mmanchola.blog.util.MarkdownParser;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mmanchola.blog.config.security.ApplicationUserRole.ADMIN;
import static com.mmanchola.blog.model.PostStatus.*;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final PersonService personService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final PostService postService;
    private final ImageService imageService;
    private final PrettyTime prettyTime;

    @Autowired
    public AdminController(PersonService personService, TagService tagService,
                           CategoryService categoryService, PostService postService,
                           ImageService imageService) {
        this.personService = personService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.postService = postService;
        this.imageService = imageService;
        this.prettyTime = new PrettyTime(new Locale("es"));
    }

    @ModelAttribute("member")
    public Person findLoggedUser(Authentication authentication) {
        String username = authentication.getName();
        return personService.get(username);
    }

    @ModelAttribute("genderMap")
    public Map<String, String> mapGenderToSpanish() {
        Map<String, String> map = new HashMap<>();
        map.put("MALE", "Hombre");
        map.put("FEMALE", "Mujer");
        map.put("OTHER", "Otro");
        return map;
    }

    @ModelAttribute("statusMap")
    public Map<String, String> mapStatusToSpanish() {
        Map<String, String> map = new HashMap<>();
        map.put("draft", "Edición");
        map.put("outdated", "Baja");
        map.put("published", "Publicado");
        return map;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String displayAdminPage(@ModelAttribute("member") Person user, Model model) {
        model.addAttribute("member", user);
        model.addAttribute("admins", personService.getAdmins());
        return "admin";
    }

    @GetMapping("category")
    public String displayAdminCategory(@ModelAttribute("member") Person user, Model model) {
        model.addAttribute("member", user);
        List<Category> categories = categoryService.getAll();
        Map<Integer, String> parentMap = new HashMap<>();
        for (Category c : categories) {
            parentMap.put(c.getId(), c.getSlug());
        }
        model.addAttribute("categories", categories);
        model.addAttribute("parentMap", parentMap);
        return "admin-category";
    }

    @GetMapping("category/new")
    public String showNewCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryService.getAll());
        return "admin-create-category";
    }

    @PostMapping("category/new")
    public String saveNewCategory(Category category, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("category", category);
        model.addAttribute("method", "POST");
        try {
            categoryService.add(category);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAll());
            return "admin-create-category";
        }
        redirectAttributes.addFlashAttribute("message", "La nueva categoría fue guardado exitosamente");
        return "redirect:/admin/category";
    }

    @PostMapping("category/delete/{slug}")
    public String deleteCategory(@PathVariable("slug") String slug, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.removePosts(slug);
            categoryService.delete(slug);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Parece que estás intentando eliminar una categoría padre. " +
                            "Elimina primero todas las categorías asociadas para hacer esto.");
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/category";
    }

    @GetMapping("category/update/{slug}")
    public String showUpdateCategoryForm(@PathVariable("slug") String slug, Model model) {
        Category category = categoryService.getBySlug(slug);
        model.addAttribute("slug", slug);
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("method", "PUT");
        return "admin-create-category";
    }

    @PostMapping("category/update/{slug}")
    public String updateCategory(@PathVariable("slug") String slug, Category category, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("category", category);
        try {
            categoryService.update(slug, category);
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return String.format("redirect:/admin/category/update/%s", slug);
        }
        redirectAttributes.addFlashAttribute("message", "La categoría fue actualizada exitosamente");
        return "redirect:/admin/category";
    }

    @GetMapping("post")
    public String displayAdminPost(@ModelAttribute("member") Person user,
                                   @ModelAttribute("statusMap") Map<String, String> statusMap,
                                   Model model) {
        model.addAttribute("member", user);
        model.addAttribute("statusMap", statusMap);
        List<Post> posts = postService.getAll();
        Map<Integer, String> parentMap = new HashMap<>();
        for (Post p : posts) {
            parentMap.put(p.getId(), p.getSlug());
        }
        model.addAttribute("posts", posts);
        model.addAttribute("parentMap", parentMap);
        return "admin-post";
    }

    @GetMapping("post/new")
    public String showNewPostForm(Model model) {
        PostForm postForm = new PostForm();
        model.addAttribute("postForm", postForm);
        model.addAttribute("posts", postService.getAll());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAll());
        return "admin-create-post";
    }

    @PostMapping("post/new")
    public String saveNewPost(@ModelAttribute("member") Person member,
                              PostForm postForm,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        model.addAttribute("method", "POST");
        model.addAttribute("post", postForm);
        try {
            Post post = postForm.getPost();
            int categoryId = postForm.getCategoryId();
            List<Integer> tagIds = postForm.getTagIds();
            post.setStatus(DRAFT.toString());
            postService.add(post, member.getEmail());
            postService.addCategory(post.getSlug(), categoryId);
            postService.addTags(post.getSlug(), tagIds);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAll());
            model.addAttribute("tags", tagService.getAll());
            return "admin-create-post";
        }
        redirectAttributes.addFlashAttribute("message", "El nuevo post fue guardado exitosamente");
        return "redirect:/admin/post";
    }

    @PostMapping("post/delete/{slug}")
    public String deletePost(@PathVariable("slug") String slug, Model model, RedirectAttributes redirectAttributes) {
        try {
            postService.removeTags(slug);
            postService.removeCategory(slug);
            postService.delete(slug);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Parece que estás intentando eliminar un post padre. " +
                            "Elimina primero todos los predecesores para levar a cabo esta acción.");
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/post";
    }

    @GetMapping("post/update/{slug}")
    public String showUpdatePostForm(@PathVariable("slug") String slug, Model model, RedirectAttributes redirectAttributes) {
        Post post;
        try {
            post = postService.getBySlug(slug);
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "error";
        }
        List<Integer> tagIds = postService.getTags(slug);
        int categoryId = postService.getCategory(slug).orElse(0);
        PostForm postForm = new PostForm(post, tagIds, categoryId);
        model.addAttribute("slug", slug);
        model.addAttribute("postForm", postForm);
        model.addAttribute("posts", postService.getAll());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAll());
        model.addAttribute("method", "PUT");
        return "admin-create-post";
    }

    @PostMapping("post/update/{slug}")
    public String updatePost(@PathVariable("slug") String slug,
                             PostForm postForm,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        model.addAttribute("postForm", postForm);
        try {
            Post post = postForm.getPost();
            int categoryId = postForm.getCategoryId();
            List<Integer> tagIds = postForm.getTagIds();
            postService.removeCategory(slug);
            postService.addCategory(slug, categoryId);
            postService.removeTags(slug);
            postService.addTags(slug, tagIds);
            postService.update(slug, post);
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return String.format("redirect:/admin/post/update/%s", slug);
        }
        redirectAttributes.addFlashAttribute("message", "El post fue actualizado exitosamente");
        return "redirect:/admin/post";
    }

    @GetMapping("preview/{slug}")
    public String previewPost(@ModelAttribute("member") Person person,
                              @PathVariable("slug") String slug,
                              Model model) {
        model.addAttribute("person", person);
        model.addAttribute("status", "draft");
        Post post = postService.getBySlug(slug);
        post.setContent(MarkdownParser.parse(post.getContent()));
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
                .map(id -> tagService.get(id))
                .collect(Collectors.toList());
        model.addAttribute("tags", tags);
        // Post likes
        model.addAttribute("isAlreadyLiked", postService.isAlreadyLiked(slug, person.getEmail()));
        model.addAttribute("numLikes", postService.getLikes(slug));
        // Popular posts for sidebar
        model.addAttribute("popularPosts", postService.getPopular(3));
        // Popular tags for sidebar
        model.addAttribute("popularTags", tagService.getPopular(5));
        return "post";
    }

    @PostMapping("publish/{slug}")
    public String publishPost(@PathVariable("slug") String slug) {
        postService.updateStatus(slug, PUBLISHED.toString());
        postService.updatePublicationTime(slug);
        return String.format("redirect:/post/%s", slug);
    }

    @PostMapping("outdate/{slug}")
    public String outdatePost(@PathVariable("slug") String slug) {
        postService.updateStatus(slug, OUTDATED.toString());
        return "redirect:/admin/post";
    }

    @GetMapping("tag")
    public String displayAdminTag(@ModelAttribute("member") Person user, Model model) {
        model.addAttribute("member", user);
        model.addAttribute("tags", tagService.getAll());
        return "admin-tag";
    }

    @GetMapping("tag/new")
    public String showNewTagForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "admin-create-tag";
    }

    @PostMapping("tag/new")
    public String saveNewTag(Tag tag, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("tag", tag);
        model.addAttribute("method", "POST");
        try {
            tagService.add(tag);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin-create-tag";
        }
        redirectAttributes.addFlashAttribute("message", "El nuevo tag fue guardado exitosamente");
        return "redirect:/admin/tag";
    }

    @PostMapping("tag/delete/{slug}")
    public String deleteTag(@PathVariable("slug") String slug,
                            RedirectAttributes redirectAttributes) {
        try {
            tagService.removePosts(slug);
            tagService.delete(slug);
        } catch (DataIntegrityViolationException e) {
            System.out.println("1st Exception");
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Parece que estás intentando eliminar un tag sin eliminar primero sus asociaciones. " +
                            "Elimínalas primero para hacer esto.");
        } catch (ApiRequestException e) {
            System.out.println("2nd Exception");
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tag";
    }

    @GetMapping("tag/update/{slug}")
    public String showUpdateTagForm(@PathVariable("slug") String slug, Model model) {
        Tag tag = tagService.getBySlug(slug);
        model.addAttribute("slug", slug);
        model.addAttribute("tag", tag);
        model.addAttribute("method", "PUT");
        return "admin-create-tag";
    }

    @PostMapping("tag/update/{slug}")
    public String updateTag(@PathVariable("slug") String slug, Tag tag, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("tag", tag);
        try {
            tagService.update(slug, tag);
        } catch (ApiRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin-create-tag";
        }
        redirectAttributes.addFlashAttribute("message", "El tag fue actualizado exitosamente");
        return "redirect:/admin/tag";
    }

    @GetMapping("user")
    public String displayAdminUser(@ModelAttribute("member") Person user,
                                   @ModelAttribute("genderMap") Map<String, String> genderMap,
                                   Model model) {
        model.addAttribute("genderMap", genderMap);
        model.addAttribute("member", user);
        List<Person> people = personService.getAll();
        Map<String, Boolean> adminMap = new HashMap<>();
        for (Person person : people) {
            adminMap.put(person.getEmail(), personService.isAdmin(person.getEmail()));
        }
        model.addAttribute("people", people);
        model.addAttribute("adminMap", adminMap);
        return "admin-user";
    }

    @PostMapping("user/delete/{email}")
    public String deleteUser(@PathVariable("email") String email) {
        personService.deleteByEmail(email);
        return "redirect:/admin/user";
    }

    @PostMapping("user/admin/{email}")
    public String grantAdminPrivileges(@PathVariable("email") String email) {
        personService.addRole(email, ADMIN.name());
        return "redirect:/admin/user";
    }

    @PostMapping("user/reader/{email}")
    public String revokeAdminPrivileges(@PathVariable("email") String email) {
        personService.removeRole(email, ADMIN.name());
        return "redirect:/admin/user";
    }

    @GetMapping("image")
    public String displayAdminImage(Model model) throws IOException {
        model.addAttribute("imageSummaries", imageService.getObjectSummaries());
        return "admin-image";
    }

    @PostMapping("image/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @ModelAttribute("member") Person person) {
        model.addAttribute("person", person);
        // Store file(s) into S3 bucket
        try {
            imageService.uploadImage(file);
            redirectAttributes.addFlashAttribute("message",
                    "La imagen " + file.getOriginalFilename() + " fue guardada satisfactoriamente.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "La imagen " + file.getOriginalFilename() + "no pudo ser subida.");
        }
        return "redirect:/admin/image";
    }

    @PostMapping("image/delete/{key}")
    public String deleteImage(@PathVariable String key,
                              RedirectAttributes redirectAttributes) {
        try {
            // Delete image from S3 bucket
            imageService.delete(key);
            redirectAttributes.addFlashAttribute("message", "La imagen fue eliminada satisfactoriamente.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "La imagen no pudo ser eliminada");
        }
        return "redirect:/admin/image";
    }

}
