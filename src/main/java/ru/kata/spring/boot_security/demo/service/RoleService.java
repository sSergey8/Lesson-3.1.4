package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getAllRoles();
    Role findByName(String name);
    void save(Role role);
    List<Role> findByIds(List<Long> ids);
}

