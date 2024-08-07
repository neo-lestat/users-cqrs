package org.example.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "org.example.users.infrastructure")
public class UsersCudApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersCudApplication.class, args);
    }

}
