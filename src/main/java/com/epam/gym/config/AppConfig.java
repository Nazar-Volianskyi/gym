package com.epam.gym.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.epam.gym")
@PropertySource("classpath:application.properties")
public class AppConfig {
}
