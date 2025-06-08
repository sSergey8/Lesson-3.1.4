package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
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

    @GetMapping({"", "/"})
    public String adminHome(Model model, Principal principal) {
        prepareAdminModel(model, principal, new User(), null, null);
        return "admin";
    }


    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        user.setRoles(extractRoles(roleIds));

        User existingUser = userService.findById(user.getId());
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(userService.encodePassword(user.getPassword()));
        }

        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds,
                             Model model,
                             Principal principal) {

        user.setRoles(extractRoles(roleIds));

        try {
            userService.save(user);
            return "redirect:/admin";
        } catch (IllegalArgumentException e) {
            prepareAdminModel(model, principal, user, roleIds, e.getMessage());
            return "admin";
        }
    }

    @PostMapping("/delete")
    public String deleteUserPost(@RequestParam("id") int id) {
        userService.deleteById(id);
        return "redirect:/admin/";
    }



    // Вспомогательные методы
    private Set<Role> extractRoles(List<Long> roleIds) {
        return roleIds != null ? new HashSet<>(roleService.findByIds(roleIds)) : new HashSet<>();
    }

    private void prepareAdminModel(Model model, Principal principal, User editUser,
                                   List<Long> selectedRoleIds, String emailError) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("currentUser", userService.findByEmail(principal.getName()));
        model.addAttribute("user", editUser);

        if (emailError != null) {
            model.addAttribute("emailError", emailError);
            model.addAttribute("showForm", true);
        }

        if (selectedRoleIds != null) {
            model.addAttribute("selectedRoleIds", selectedRoleIds);
        }
    }
}