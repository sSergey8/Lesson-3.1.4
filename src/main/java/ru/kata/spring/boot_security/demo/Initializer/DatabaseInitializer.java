package ru.kata.spring.boot_security.demo.Initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.annotation.PostConstruct;
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

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    return roleRepository.save(new Role("ROLE_ADMIN"));
                });

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    return roleRepository.save(new Role("ROLE_USER"));
                });


        if (userRepository.findByEmail("admin@mail.ru").isEmpty()) {
            User admin = new User(
                    "Admin", "Admin",
                    "admin@mail.ru", 30,
                    passwordEncoder.encode("admin"),
                    Set.of(adminRole)
            );
            userRepository.save(admin);
        }


        if (userRepository.findByEmail("user@mail.ru").isEmpty()) {
            User user = new User(
                    "User", "User",
                    "user@mail.ru", 28,
                    passwordEncoder.encode("user"),
                    Set.of(userRole)
            );
            userRepository.save(user);
        }
    }
}