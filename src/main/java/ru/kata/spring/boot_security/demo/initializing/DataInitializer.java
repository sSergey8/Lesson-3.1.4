package ru.kata.spring.boot_security.demo.initializing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleDao;
import ru.kata.spring.boot_security.demo.repository.UserDao;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class DataInitializer {


    @Autowired
    private RoleDao roleDao;

    @PostConstruct
    public void init() {
        if (roleDao.findAll().isEmpty()) {
            roleDao.save(new Role("ROLE_USER"));
            roleDao.save(new Role("ROLE_ADMIN"));
        }
    }
}