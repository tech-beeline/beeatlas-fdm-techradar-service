/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@EntityScan("ru.beeline.techradar.domain.*")
public class TechradarBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechradarBackendApplication.class, args);
    }

}
