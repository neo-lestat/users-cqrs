package org.example.usersread.application.service;

import org.example.usersread.application.repository.UserProjectionRepository;
import org.example.usersread.application.usecase.UserProjectionUseCase;
import org.example.usersread.domain.model.User;

public class UserProjectionService implements UserProjectionUseCase {

    private final UserProjectionRepository userProjectionRepository;

    public UserProjectionService(UserProjectionRepository userProjectionRepository) {
        this.userProjectionRepository = userProjectionRepository;
    }

    @Override
    public void saveOrUpdate(User user) {
        if (userProjectionRepository.usernameExist(user.username())) {
            userProjectionRepository.update(user.username(), user);
        } else {
            userProjectionRepository.save(user);
        }
    }

    @Override
    public void delete(String username) {
        userProjectionRepository.delete(username);
    }
}
