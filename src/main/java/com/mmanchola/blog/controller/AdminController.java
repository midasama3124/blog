package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Category;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.model.Post;
import com.mmanchola.blog.model.Tag;
import com.mmanchola.blog.service.CategoryService;
import com.mmanchola.blog.service.PersonService;
import com.mmanchola.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mmanchola.blog.config.security.ApplicationUserRole.ADMIN;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final PersonService personService;
    private final TagService tagService;
    private final CategoryService categoryService;

    @Autowired
    public AdminController(PersonService personService, TagService tagService, CategoryService categoryService) {
        this.personService = personService;
        this.tagService = tagService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("member")
    public Person findLoggedUser(Authentication authentication) {
        String username = authentication.getName();
        return personService.getByEmail(username).get();
    }

    @ModelAttribute("genderMap")
    public Map<String, String> mapGenderToSpanish() {
        Map<String, String> map = new HashMap<>();
        map.put("MALE", "Hombre");
        map.put("FEMALE", "Mujer");
        map.put("OTHER", "Otro");
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
            return "admin-create-category";
        }
        redirectAttributes.addFlashAttribute("message", "La nueva categoría fue guardado exitosamente");
        return "redirect:/admin/category";
    }

    @PostMapping("category/delete/{slug}")
    public String deleteCategory(@PathVariable("slug") String slug, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(slug);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Parece que estás intentando eliminar una categoría padre. Elimina primero todas las categorías asociadas para hacer esto.");
        } catch (ApiRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/category";
    }

    @GetMapping("category/update/{slug}")
    public String showUpdateCategoryForm(@PathVariable("slug") String slug, Model model) {
        Category category = categoryService.getBySlug(slug).get();
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
    public String displayAdminPost(@ModelAttribute("member") Person user, Model model) {
        model.addAttribute("member", user);
        return "admin-post";
    }

    @GetMapping("post/new")
    public String showNewPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "admin-create-post";
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
    public String deleteTag(@PathVariable("slug") String slug) {
        tagService.delete(slug);
        return "redirect:/admin/tag";
    }

    @GetMapping("tag/update/{slug}")
    public String showUpdateTagForm(@PathVariable("slug") String slug, Model model) {
        Tag tag = tagService.getBySlug(slug).get();
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

}
