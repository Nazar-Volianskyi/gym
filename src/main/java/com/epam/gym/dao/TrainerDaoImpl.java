package com.epam.gym.dao;

import com.epam.gym.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainerDaoImpl implements TrainerDao {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainer create(Trainer trainer) {
        entityManager.persist(trainer);
        log.info("Trainer created with id {}", trainer.getId());
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Trainer merged = entityManager.merge(trainer);
        log.info("Trainer updated with id {}", merged.getId());
        return merged;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        log.info("Fetching trainer with id {}", id);
        Trainer trainer = entityManager.find(Trainer.class, id);
        if (trainer == null) {
            log.warn("Trainer with id {} not found", id);
        }
        return Optional.ofNullable(trainer);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        log.info("Fetching trainer with username {}", username);
        TypedQuery<Trainer> query = entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class);
        query.setParameter("username", username);
        List<Trainer> result = query.getResultList();
        if (result.isEmpty()) {
            log.warn("Trainer with username {} not found", username);
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Trainer> findAll() {
        log.info("Fetching all trainers");
        TypedQuery<Trainer> query = entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class);
        return query.getResultList();
    }

    @Override
    public List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername) {
        log.info("Fetching trainers not assigned to trainee with username {}", traineeUsername);
        TypedQuery<Trainer> query = entityManager.createQuery(
                "SELECT tr FROM Trainer tr WHERE tr.user.isActive = true AND tr NOT IN " +
                        "(SELECT t2 FROM Trainee te JOIN te.trainers t2 WHERE te.user.username = :username)",
                Trainer.class);
        query.setParameter("username", traineeUsername);
        return query.getResultList();
    }

    @Override
    public boolean existsByFullName(String firstName, String lastName) {
        log.info("Checking whether a trainer with name {} {} exists", firstName, lastName);
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(t) FROM Trainer t WHERE t.user.firstName = :firstName AND t.user.lastName = :lastName",
                Long.class);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        return query.getSingleResult() > 0;
    }
}