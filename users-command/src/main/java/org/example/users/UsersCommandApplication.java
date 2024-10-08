package org.example.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "org.example.users.infrastructure")
public class UsersCommandApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersCommandApplication.class, args);
    }

}
