package org.example.usersread.application.repository;

import org.example.usersread.domain.model.PagedResult;
import org.example.usersread.domain.model.User;

public interface UserReadRepository {

    User findByUsername(String username);

    PagedResult<User> findAll(int pageNumber, int pageSize);
}
