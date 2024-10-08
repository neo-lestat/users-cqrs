package org.example.users.application.usecase;

import org.example.users.domain.model.User;

import java.util.List;

public interface GenerateUsersUseCase {

    List<User> generateUsers(int number);

}
