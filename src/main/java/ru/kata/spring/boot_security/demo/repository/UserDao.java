package ru.kata.spring.boot_security.demo.repository;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDao {
    List<User> getAllUsers();
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(int id);
    User getUserById(int id);
    User getByName(String name);
}
