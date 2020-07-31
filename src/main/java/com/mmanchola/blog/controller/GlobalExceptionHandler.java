package com.mmanchola.blog.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileSizeLimitExceeded(MaxUploadSizeExceededException exc,
                                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Límite de peso para imagen fue excedido (1 MB).");
        return "redirect:/admin/image";
    }

}
