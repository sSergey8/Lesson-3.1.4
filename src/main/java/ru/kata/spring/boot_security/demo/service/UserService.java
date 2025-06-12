package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findById(int id);
    List<User> findAll();
    void save(User user);
    void update(User user);
    void deleteById(int id);
    User findByEmail(String email);
}