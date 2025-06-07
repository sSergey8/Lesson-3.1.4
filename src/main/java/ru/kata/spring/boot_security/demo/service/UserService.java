package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User findById(int id);
    List<User> findAll();
    void save(User user);
    void update(User user);
    void deleteById(int id);
    String encodePassword(String rawPassword);
}