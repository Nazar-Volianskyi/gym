package com.epam.gym.util;


import com.epam.gym.model.Trainee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProfileInitializerTest {

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private UsernameUniquenessChecker usernameUniquenessChecker;

    @InjectMocks
    private UserProfileInitializer initializer;

    @Test
    void initialize_shouldSetUsernamePasswordAndActiveStatus() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Nazar");
        trainee.setLastName("Volianskyi");

        when(usernameGenerator.generate(eq("Nazar"), eq("Volianskyi"),
                any())).thenReturn("Nazar.Volianskyi");

        initializer.initialize(trainee);

        assertEquals("Nazar.Volianskyi", trainee.getUsername());
        assertNotNull(trainee.getPassword());
        assertEquals(10, trainee.getPassword().length());
        assertTrue(trainee.isActive());

    }


}
