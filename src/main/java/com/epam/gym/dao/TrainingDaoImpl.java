package com.epam.gym.dao;

import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingDaoImpl implements TrainingDao {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Training create(Training training) {
        entityManager.persist(training);
        log.info("Training created with id {}", training.getId());
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.debug("Fetching training with id {}", id);
        Training training = entityManager.find(Training.class, id);
        if (training == null) {
            log.warn("Training with id {} not found", id);
        }
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> findAll() {
        log.debug("Fetching all trainings");
        TypedQuery<Training> query = entityManager.createQuery("SELECT t FROM Training t", Training.class);
        return query.getResultList();
    }

    @Override
    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate,
                                               LocalDate toDate, String trainingTypeName) {
        log.debug("Fetching trainee trainings for username {} with criteria", traineeUsername);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(training.get("trainee").get("user").get("username"), traineeUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
        }

        if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            predicates.add(cb.equal(training.get("trainingType").get("trainingTypeName"), trainingTypeName));
        }

        query.where(predicates.toArray(new Predicate[0]));

        List<Training> result = entityManager.createQuery(query).getResultList();
        log.debug("Found {} trainings for trainee {}", result.size(), traineeUsername);
        return result;
    }

    @Override
    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate,
                                               LocalDate toDate) {
        log.debug("Fetching trainer trainings for username {} with criteria", trainerUsername);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(training.get("trainer")
                .get("user")
                .get("username"), trainerUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                    training.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(
                    training.get("trainingDate"), toDate));
        }


        query.where(predicates.toArray(new Predicate[0]));
        List<Training> result = entityManager.createQuery(query).getResultList();
        log.debug("Found {} trainings for trainer {}", result.size(), trainerUsername);
        return result;
    }
}
