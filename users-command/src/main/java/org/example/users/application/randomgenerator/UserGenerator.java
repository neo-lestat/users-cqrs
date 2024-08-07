package org.example.users.application.randomgenerator;

import org.example.users.domain.model.User;

import java.util.List;

public interface UserGenerator {
    List<User> generate(int number);
}
