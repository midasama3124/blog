package com.mmanchola.blog.controller;

import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.model.Tag;
import com.mmanchola.blog.service.PersonService;
import com.mmanchola.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final PersonService personService;
    private final TagService tagService;

    @Autowired
    public AdminController(PersonService personService, TagService tagService) {
        this.personService = personService;
        this.tagService = tagService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String displayAdminPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        Person user = personService.getByEmail(username).get();
        model.addAttribute("member", user);
        model.addAttribute("admins", personService.getAdmins());
        return "admin";
    }

    @GetMapping("category")
    public String displayAdminCategory(Model model) {
        return "admin-category";
    }

    @GetMapping("category/new")
    public String showNewCategoryForm() {
        return "admin-create-category";
    }

    @GetMapping("post")
    public String displayAdminPost(Model model) {
        return "admin-post";
    }

    @GetMapping("post/new")
    public String showNewPostForm() {
        return "admin-create-post";
    }

    @GetMapping("tag")
    public String displayAdminTag(Model model) {
        model.addAttribute("tags", tagService.getAll());
        return "admin-tag";
    }

    @PostMapping("tag/delete/{slug}")
    public String deleteTag(@PathVariable("slug") String slug) {
        System.out.println(slug);
        tagService.delete(slug);
        return "redirect:/admin/tag";
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
        redirectAttributes.addFlashAttribute("message", "El nuevo tag fue guardado satisfactoriamente");
        return "redirect:/admin/tag";
    }

    @GetMapping("tag/update/{slug}")
    public String showUpdateTagForm(@PathVariable("slug") String slug, Model model) {
        // TODO: Handle NULL exception
        Tag tag = tagService.getBySlug(slug).orElse(null);
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
    public String displayAdminUser(Model model) {
        return "admin-user";
    }

}
