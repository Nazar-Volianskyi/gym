package com.epam.gym.storage;

import com.epam.gym.model.Training;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TrainingStorage {

    private final Map<Long, Training> storage = new HashMap<>();

    private final AtomicLong idSequence = new AtomicLong(1);

    public Map<Long, Training>  getStorage() {
        return storage;
    }

    public Long nextId() {
        return idSequence.getAndIncrement();
    }

    public void setIdSequenceStart(long value) {
        idSequence.set(value);
    }
}
