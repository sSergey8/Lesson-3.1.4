package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Получить всех пользователей
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // Получить текущего администратора
    @GetMapping("/current")
    public User getCurrentUser(Principal principal) {
        return userService.findByEmail(principal.getName());
    }

    // Создать пользователя
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Извлечь ID ролей из переданных ролей
            List<Long> roleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());

            // Получить роли из базы по ID
            List<Role> resolvedRoles = roleService.findByIds(roleIds);

            // Присвоить пользователю роли из базы
            user.setRoles(new HashSet<>(resolvedRoles));

            handlePasswordEncoding(user);
            userService.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Обновить пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        User existing = userService.findById(id);

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(userService.encodePassword(user.getPassword()));
        }

        userService.update(user);
        return ResponseEntity.ok(user);
    }

    // Удалить пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Получить все роли
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // Вспомогательный метод
    private void handlePasswordEncoding(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(userService.encodePassword(user.getPassword()));
        }
    }
}
