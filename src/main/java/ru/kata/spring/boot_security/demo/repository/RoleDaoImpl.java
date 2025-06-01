package ru.kata.spring.boot_security.demo.repository;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> findAll() {
        return entityManager.createQuery("from Role", Role.class).getResultList();
    }

    @Override
    public Role findByName(String name) {
        return entityManager.createQuery("from Role r where r.name = :name", Role.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    @Override
    public Role findById(Long id) {
        return entityManager.find(Role.class, id);
    }

    @Override
    @Transactional
    public Role save(Role role) {
        entityManager.persist(role);
        return role;
    }
}

