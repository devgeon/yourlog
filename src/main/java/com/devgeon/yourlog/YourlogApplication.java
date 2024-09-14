package com.devgeon.yourlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableJpaAuditing
@SpringBootApplication
@RestController
public class YourlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(YourlogApplication.class, args);
    }

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

}
