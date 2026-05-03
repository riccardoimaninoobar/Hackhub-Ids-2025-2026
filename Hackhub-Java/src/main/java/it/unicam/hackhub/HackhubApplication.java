package it.unicam.hackhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HackhubApplication {
    public static void main(String[] args) {
        SpringApplication.run(HackhubApplication.class, args);
    }
}