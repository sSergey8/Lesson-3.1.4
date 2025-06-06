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
    public String adminHome(Model model) {
        // Можно добавить в модель список всех пользователей, ролей и т.д.
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("user", new User());
        return "admin";  // имя шаблона admin.html
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

//    @GetMapping("/new")
//    public String NewUserForm(Model model) {
//        model.addAttribute("user", new User());
//        model.addAttribute("roles", roleService.getAllRoles()); // если роли динамически
//        return "new";
//    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute("newUser") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            roles = new HashSet<>(roleService.findByIds(roleIds));
        }
        user.setRoles(roles);

        // Тут нужно обязательно кодировать пароль перед сохранением
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUserPost(@RequestParam("id") int id) {
        userService.deleteById(id);
        return "redirect:/admin/";
    }

}