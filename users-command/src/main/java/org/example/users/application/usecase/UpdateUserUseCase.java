package org.example.users.application.usecase;

import org.example.users.domain.model.User;

public interface UpdateUserUseCase {

    User updateUser(String username, User user);

}
