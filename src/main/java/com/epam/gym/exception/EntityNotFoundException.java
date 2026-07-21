package com.epam.gym.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Long id) {
        super("%s with id %d was not found".formatted(entityName, id));
    }
}
