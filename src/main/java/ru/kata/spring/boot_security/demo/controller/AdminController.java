package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showAllUsers(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", currentUser);
        return "admin";  // предполагается, что у тебя есть admin.html с таблицей пользователей
    }

    @GetMapping("/new")
    public String newUserForm(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("currentUser", currentUser);
        return "newUser";  // форма создания пользователя (thymeleaf)
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute User user,
                             @RequestParam("rolesSelected") Set<String> rolesSelected) {
        user.setRoles(roleService.getRolesByNames(rolesSelected));
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String editUserForm(@RequestParam("id") int id, Model model, @AuthenticationPrincipal User currentUser) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("currentUser", currentUser);
        return "editUser";  // форма редактирования
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam("rolesSelected") Set<String> rolesSelected) {
        user.setRoles(roleService.getRolesByNames(rolesSelected));
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}