package com.epam.gym.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void generate_shouldReturnStringOfLength10(){
        String password = PasswordGenerator.generate();
        assertEquals(10, password.length());
    }

    @Test
    void generate_shouldContainOnlyAllowedCharacters(){
        String password = PasswordGenerator.generate();
        assertTrue(password.chars().allMatch(Character::isLetterOrDigit));
    }

    @RepeatedTest(5)
    void generate_shouldProduceDifferentValuesAcrossCalls(){
        String first = PasswordGenerator.generate();
        String second = PasswordGenerator.generate();

        assertNotEquals(first, second);
    }


}
