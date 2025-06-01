package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping({"/", "/users"})
    public String listUsers(Model model, Principal principal) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("username", principal.getName());
        return "users";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "new";
    }

    @PostMapping("/users")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam("roleName") String roleName) {

        Role role = roleService.getRoleByName(roleName);
        user.setRoles(Set.of(role));

        userService.addUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit")
    public String editUserForm(@RequestParam("id") int id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "edit";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleIds.stream()
                    .map(roleService::getRoleById)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            user.setRoles(new HashSet<>());
        }

        userService.updateUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}