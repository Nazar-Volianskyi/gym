package com.epam.gym.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameGeneratorTest {

    private final UsernameGenerator usernameGenerator = new UsernameGenerator();

    @Test
    void generate_shouldReturnBaseUsername_whenNoConflict(){
        String result = usernameGenerator.generate("Nazar", "Volianskyi", c -> false);

        assertEquals("Nazar.Volianskyi", result);
    }

    @Test
    void generate_shouldReturnBaseUsername_whenBaseUsernameExist(){
        String result = usernameGenerator.generate("Nazar", "Volianskyi", c -> c.equals("Nazar.Volianskyi"));

        assertEquals("Nazar.Volianskyi1", result);
    }

    @Test
    void generate_shouldIncrementSerial_whenSomeConflictsExist(){
        String result = usernameGenerator.generate("Nazar", "Volianskyi",
                c -> c.equals("Nazar.Volianskyi") || c.equals("Nazar.Volianskyi1"));
        assertEquals("Nazar.Volianskyi2", result);
    }
}
