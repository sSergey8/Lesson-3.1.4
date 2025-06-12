package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ViewController {

    @GetMapping("/user/")
    public String userPage(CsrfToken csrfToken, Model model) {
        model.addAttribute("_csrf", csrfToken);
        return "user";
    }

    @GetMapping("/admin/")
    public String adminPage(CsrfToken csrfToken, Model model) {
        model.addAttribute("_csrf", csrfToken);
        return "admin";
    }
}
