package org.example.usersread.application.repository;

import org.example.usersread.domain.model.User;

public interface UserProjectionRepository {

    boolean usernameExist(String username);

    User save(User user);

    User update(String username, User user);

    void delete(String username);
}
