package ru.kata.spring.boot_security.demo.Initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseInitializer(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        // 🔹 Убедимся, что роли есть
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    System.out.println("🛠 Создаётся роль ROLE_ADMIN");
                    return roleRepository.save(new Role("ROLE_ADMIN"));
                });

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    System.out.println("🛠 Создаётся роль ROLE_USER");
                    return roleRepository.save(new Role("ROLE_USER"));
                });

        // 🔹 Добавим админа, если его нет
        if (userRepository.findByEmail("admin@mail.ru").isEmpty()) {
            User admin = new User(
                    "Admin", "Admin",
                    "admin@mail.ru", 30,
                    passwordEncoder.encode("admin"),
                    Set.of(adminRole)
            );
            userRepository.save(admin);
            System.out.println("✅ Пользователь admin@mail.ru создан");
        }

        // 🔹 Добавим обычного пользователя, если его нет
        if (userRepository.findByEmail("user@mail.ru").isEmpty()) {
            User user = new User(
                    "User", "User",
                    "user@mail.ru", 28,
                    passwordEncoder.encode("user"),
                    Set.of(userRole)
            );
            userRepository.save(user);
            System.out.println("✅ Пользователь user@mail.ru создан");
        }

        System.out.println("⚙️ Инициализация завершена");
    }
}