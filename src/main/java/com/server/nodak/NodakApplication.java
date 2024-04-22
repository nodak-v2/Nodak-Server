package com.server.nodak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NodakApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodakApplication.class, args);
    }

}
