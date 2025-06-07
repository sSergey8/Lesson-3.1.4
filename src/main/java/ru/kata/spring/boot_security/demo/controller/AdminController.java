package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Метод для главной страницы админа
    @GetMapping({"", "/"})
    public String adminHome(Model model,
                            @RequestParam(required = false) Integer editUserId,
                            Principal principal) {
        List<User> users = userService.findAll();
        List<Role> roles = roleService.getAllRoles();

        User editUser = (editUserId != null) ? userService.findById(editUserId) : new User();

        // ⚠ Получаем текущего залогиненного пользователя по email
        User currentUser = userService.findByEmail(principal.getName());

        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        model.addAttribute("user", editUser);
        model.addAttribute("currentUser", currentUser); // вот это добавлено

        return "admin";
    }

    // Пример метода для вывода конкретного пользователя
    @GetMapping("/edit/{id}")
    public String getUser(@PathVariable("id") int id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "error";
        }
        model.addAttribute("user", user);
        return "user"; // или "edit-user", как у тебя шаблон называется
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("editUser") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            roles = new HashSet<>(roleService.findByIds(roleIds));
        }
        user.setRoles(roles);

        // ⚠️ Обязательно: получить существующего пользователя для сравнения пароля
        User existingUser = userService.findById(user.getId());

        // Если пароль пустой, сохраняем старый
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            // Иначе — зашифровать новый
            user.setPassword(userService.encodePassword(user.getPassword()));
        }

        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds,
                             Model model,
                             Principal principal,
                             HttpServletRequest request) {

        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            roles = new HashSet<>(roleService.findByIds(roleIds));
        }
        user.setRoles(roles);

        try {
            userService.save(user);
            return "redirect:/admin";
        } catch (IllegalArgumentException e) {
            // Сохраняем все необходимые атрибуты
            model.addAttribute("users", userService.findAll());
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("currentUser", userService.findByEmail(principal.getName()));
            model.addAttribute("emailError", e.getMessage());

            // Добавляем параметр для JavaScript
            model.addAttribute("showForm", true);

            // Сохраняем выбранные роли
            if (roleIds != null) {
                model.addAttribute("selectedRoleIds", roleIds);
            }

            return "admin";
        }
    }

    @PostMapping("/delete")
    public String deleteUserPost(@RequestParam("id") int id) {
        userService.deleteById(id);
        return "redirect:/admin/";
    }

}