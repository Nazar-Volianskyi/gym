package com.epam.gym.dao;

import com.epam.gym.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        log.info("Fetching training type with name {}", name);
        TypedQuery<TrainingType> query = entityManager.createQuery(
                "SELECT tt FROM TrainingType tt WHERE tt.trainingTypeName = :name", TrainingType.class);
        query.setParameter("name", name);
        List<TrainingType> result = query.getResultList();
        if (result.isEmpty()) {
            log.warn("Training type with name {} not found", name);
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<TrainingType> findAll() {
        log.info("Fetching all training types");
        TypedQuery<TrainingType> query = entityManager.createQuery("SELECT tt FROM TrainingType tt", TrainingType.class);
        return query.getResultList();
    }
}