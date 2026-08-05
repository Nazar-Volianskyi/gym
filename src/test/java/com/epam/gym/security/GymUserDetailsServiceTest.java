package com.epam.gym.security;

import com.epam.gym.dao.UserDao;
import com.epam.gym.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymUserDetailsServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private GymUserDetailsService gymUserDetailsService;

    private User buildUser(String username, String password, boolean active) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(active);
        return user;
    }

    @Test
    void loadUserByUsername_shouldReturnEnabledUserDetails_whenUserIsActive() {
        when(userDao.findByUsername("Nazar.Volianskyi"))
                .thenReturn(Optional.of(buildUser("Nazar.Volianskyi", "password", true)));

        UserDetails result = gymUserDetailsService.loadUserByUsername("Nazar.Volianskyi");

        assertEquals("Nazar.Volianskyi", result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.isEnabled());
    }

    @Test
    void loadUserByUsername_shouldReturnDisabledUserDetails_whenUserIsInactive() {
        when(userDao.findByUsername("Nazar.Volianskyi"))
                .thenReturn(Optional.of(buildUser("Nazar.Volianskyi",
                        "secret123", false)));

        UserDetails result = gymUserDetailsService.loadUserByUsername("Nazar.Volianskyi");

        assertFalse(result.isEnabled());
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userDao.findByUsername("nothing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> gymUserDetailsService.loadUserByUsername("nothing"));
    }
}
