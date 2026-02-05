package com.zalmuk.swwim.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * SWWIM API Server
 * Swimming Training Management Application
 *
 * @author SWWIM Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class SwwimApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwwimApiApplication.class, args);
    }

}
