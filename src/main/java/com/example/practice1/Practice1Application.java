package com.example.practice1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Practice1Application {

    public static void main(String[] args) {
        SpringApplication.run(Practice1Application.class, args);
    }

}
