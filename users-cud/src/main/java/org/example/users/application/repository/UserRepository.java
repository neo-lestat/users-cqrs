package org.example.users.application.repository;

import org.example.users.domain.model.User;

import java.util.List;

public interface UserRepository {

    User save(User user);

    List<User> save(List<User> users);

    User update(String username, User user);

    void delete(String username);
}
