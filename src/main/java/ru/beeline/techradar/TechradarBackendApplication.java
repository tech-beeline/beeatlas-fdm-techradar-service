package ru.beeline.techradar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class TechradarBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechradarBackendApplication.class, args);
    }

}
