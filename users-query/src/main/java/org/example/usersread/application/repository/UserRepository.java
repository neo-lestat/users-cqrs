package org.example.usersread.application.repository;

import org.example.usersread.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    boolean usernameExist(String username);

    User findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    User save(User user);

    User update(String username, User user);

    void delete(String username);
}
