package ru.kata.spring.boot_security.demo.controller;

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

    // Получить только пользователей с ролью USER (и без ADMIN)
    @GetMapping("/filtered")
    public List<User> getOnlyUsersWithoutAdmin() {
        return userService.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_USER")))
                .filter(user -> user.getRoles().stream().noneMatch(r -> r.getName().equals("ROLE_ADMIN")))
                .collect(Collectors.toList());
    }
}
