package com.epam.gym.util;

import com.epam.gym.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProfileInitializer {

    private UsernameGenerator usernameGenerator;
    private UsernameUniquenessChecker usernameUniquenessChecker;

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setUsernameUniquenessChecker(UsernameUniquenessChecker checker) {
        this.usernameUniquenessChecker = checker;
    }

    public void initialize(User user) {
        String username = usernameGenerator.generate(
                user.getFirstName(),
                user.getLastName(),
                usernameUniquenessChecker::exists
        );
        user.setUsername(username);
        user.setPassword(PasswordGenerator.generate());
        user.setActive(true);
    }
}