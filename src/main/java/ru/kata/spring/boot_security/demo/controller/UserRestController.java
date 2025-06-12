package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // Получить текущего пользователя
    @GetMapping("/current")
    public User getCurrentUser(Principal principal) {
        return userService.findByEmail(principal.getName());
    }

    // Получить всех пользователей с ролью USER (без ADMIN)
    @GetMapping
    public List<User> getOnlyUsersWithRoleUser() {
        return userService.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> "ROLE_USER".equals(role.getName())))
                .collect(Collectors.toList());
    }
}
