package ru.kata.spring.boot_security.demo.Initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Set;

@Configuration
public class DatabaseInitializer {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public DatabaseInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (userService.getUserByName("admin") != null) {
            return;
        }

        Role adminRole = new Role("ROLE_ADMIN");
        Role userRole = new Role("ROLE_USER");

        roleService.saveRole(adminRole);
        roleService.saveRole(userRole);

        // Создаем admin пользователя
        User admin = new User("admin", "adminCar", "admin", Set.of(adminRole));
        userService.addUser(admin);
    }
}