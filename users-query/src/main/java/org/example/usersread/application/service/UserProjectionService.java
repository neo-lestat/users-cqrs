package org.example.usersread.application.service;

import org.example.usersread.application.repository.UserProjectionRepository;
import org.example.usersread.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserProjectionService {

    private final UserProjectionRepository userProjectionRepository;

    public UserProjectionService(UserProjectionRepository userProjectionRepository) {
        this.userProjectionRepository = userProjectionRepository;
    }

    public void saveOrUpdate(User user) {
        if (userProjectionRepository.usernameExist(user.username())) {
            userProjectionRepository.update(user.username(), user);
        } else {
            userProjectionRepository.save(user);
        }
    }

    public void delete(String username) {
        userProjectionRepository.delete(username);
    }
}
