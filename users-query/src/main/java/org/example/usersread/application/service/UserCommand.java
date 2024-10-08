package org.example.usersread.application.service;

import org.example.usersread.domain.model.User;

public interface UserCommand {

    void saveOrUpdate(User user);
    void delete(String username);

}
