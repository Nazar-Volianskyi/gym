package com.epam.gym.storage;

import com.epam.gym.model.Trainee;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TraineeStorage {

    private final Map<Long, Trainee> storage = new HashMap<>();

    private final AtomicLong idSequence = new AtomicLong(1);

    public Map<Long, Trainee> getStorage() {
        return storage;
    }

    public Long nextId() {
        return idSequence.getAndIncrement();
    }

    public void setIdSequenceStart(long value) {
        idSequence.set(value);
    }
}
