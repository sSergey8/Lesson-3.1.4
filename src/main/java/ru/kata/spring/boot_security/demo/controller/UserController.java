package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"", "/"})
    public String userPage(Model model, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());

        List<User> allUsers = userService.findAll();

        List<User> filteredUsers = allUsers.stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER")))
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN")))
                .collect(Collectors.toList());  // <- здесь заменили на collect(Collectors.toList())

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", filteredUsers);

        return "user";
    }
}
