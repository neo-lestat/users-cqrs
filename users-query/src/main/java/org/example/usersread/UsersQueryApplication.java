package org.example.usersread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "org.example.usersread.infrastructure")
public class UsersQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersQueryApplication.class, args);
    }

}
