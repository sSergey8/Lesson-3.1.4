package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserDao;

import javax.persistence.NoResultException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.addUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userDao.getUserById(user.getId());

        // Обновляем имя и машину
        existingUser.setName(user.getName());
        existingUser.setCar(user.getCar());

        // Обновляем пароль, только если введено новое значение
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Обновляем роли
        existingUser.setRoles(user.getRoles());

        // Сохраняем изменения
        userDao.updateUser(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        userDao.deleteUser(id);
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userDao.getByName(username);
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

    }

//    @Override
//    public User findByUsername(String username) {
//        return userDao.getByName(username);
//    }
}
