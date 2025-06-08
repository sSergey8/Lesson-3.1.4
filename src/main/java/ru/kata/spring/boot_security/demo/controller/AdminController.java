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
    public String adminHome(Model model,
                            @RequestParam(required = false) Integer editUserId,
                            Principal principal) {
        User editUser = (editUserId != null) ? userService.findById(editUserId) : new User();
        prepareAdminModel(model, principal, editUser, null, null);
        return "admin";
    }

    @GetMapping("/edit/{id}")
    public String getUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "user";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("editUser") User user,
                             @RequestParam(value = "roles", required = false)
                             List<Long> roleIds) {
        Set<Role> roles = extractRoles(roleIds);
        user.setRoles(roles);

        User existingUser = userService.findById(user.getId());
        updatePasswordIfChanged(user, existingUser);

        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds,
                             Model model,
                             Principal principal) {

        Set<Role> roles = extractRoles(roleIds);
        user.setRoles(roles);

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

    private void updatePasswordIfChanged(User user, User existingUser) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(userService.encodePassword(user.getPassword()));
        }
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