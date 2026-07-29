package com.epam.gym.dao;

import com.epam.gym.model.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TraineeDaoImpl implements TraineeDao {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainee create(Trainee trainee) {
        entityManager.persist(trainee);
        log.info("Trainee created with id {}", trainee.getId());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee merged = entityManager.merge(trainee);
        log.info("Trainee updated with id {}", merged.getId());
        return merged;
    }

    @Override
    public void delete(Trainee trainee) {
        entityManager.remove(entityManager.contains(trainee) ? trainee : entityManager.merge(trainee));
        log.info("Trainee deleted with id {}", trainee.getId());
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        log.debug("Fetching trainee with id {}", id);
        Trainee trainee = entityManager.find(Trainee.class, id);
        if (trainee == null) {
            log.warn("Trainee with id {} not found", id);
        }
        return Optional.ofNullable(trainee);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        log.debug("Fetching trainee with username {}", username);
        TypedQuery<Trainee> query = entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class);
        query.setParameter("username", username);
        List<Trainee> result = query.getResultList();
        if (result.isEmpty()) {
            log.warn("Trainee with username {} not found", username);
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Trainee> findAll() {
        log.debug("Fetching all trainees");
        TypedQuery<Trainee> query = entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class);
        return query.getResultList();
    }
}