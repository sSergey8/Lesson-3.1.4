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

//@Configuration
//public class DatabaseInitializer {
//
//    private final UserService userService;
//    private final RoleService roleService;
//
//    @Autowired
//    public DatabaseInitializer(UserService userService, RoleService roleService) {
//        this.userService = userService;
//        this.roleService = roleService;
//    }
//
//    @PostConstruct
//    @Transactional
//    public void init() {
//        // Проверяем наличие администратора по email
//        if (userService.getUserByEmail("admin@mail.ru") != null) {
//            return; // Если админ с таким email уже есть — ничего не делаем
//        }
//
//        // Проверяем наличие ролей в БД
//        Role adminRole = roleService.getRoleByName("ROLE_ADMIN");
//        Role userRole = roleService.getRoleByName("ROLE_USER");
//
//        if (adminRole == null) {
//            adminRole = new Role("ROLE_ADMIN");
//            roleService.saveRole(adminRole);
//        }
//
//        if (userRole == null) {
//            userRole = new Role("ROLE_USER");
//            roleService.saveRole(userRole);
//        }
//
//        // Создаем администратора
//        User admin = new User(
//                "admin",           // имя
//                "firsAdmin",       // фамилия
//                "admin@mail.ru",   // email
//                30,                // возраст
//                "admin",           // пароль
//                Set.of(adminRole)  // роли
//        );
//
//        userService.addUser(admin);
//    }
//}