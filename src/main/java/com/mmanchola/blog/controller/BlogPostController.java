package com.mmanchola.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlogPostController {

  @GetMapping("/post")
  public String showBlogPost() {
    return "post";
  }

}
