package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.shareit.server")
public class ShareItServer {
    public static void main(String[] args) {
        SpringApplication.run(ShareItServer.class, args);
    }
}