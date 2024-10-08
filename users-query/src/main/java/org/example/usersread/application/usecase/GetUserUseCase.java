package org.example.usersread.application.usecase;

import org.example.usersread.domain.model.User;
import org.springframework.data.domain.Page;

public interface GetUserUseCase {

    User getUser(String username);

    Page<User> getUsers(int pageNumber, int pageSize);

}
