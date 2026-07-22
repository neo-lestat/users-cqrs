package org.example.usersread.application.repository;

import org.example.usersread.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserReadRepository {

    User findByUsername(String username);

    Page<User> findAll(Pageable pageable);
}
