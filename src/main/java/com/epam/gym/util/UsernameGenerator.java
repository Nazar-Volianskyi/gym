package com.epam.gym.util;


import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class UsernameGenerator {

    public String generate(String firstName, String lastName, Predicate<String> existsCheck) {
        String base = firstName + "." + lastName;
        String candidate = base;
        int serial = 1;
        while (existsCheck.test(candidate)) {
            candidate = base + serial;
            serial++;
        }
        return candidate;
    }
}
