package org.example.usersread.application.service;

import org.example.usersread.application.repository.UserReadRepository;
import org.example.usersread.application.usecase.UserQueryUseCase;
import org.example.usersread.domain.model.PagedResult;
import org.example.usersread.domain.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService implements UserQueryUseCase {

    private final UserReadRepository userRepository;

    public UserService(UserReadRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public PagedResult<User> getUsers(int pageNumber, int pageSize) {
        return userRepository.findAll(pageNumber, pageSize);
    }

    @Override
    public Map<String, Map<String, Map<String, List<User>>>> getTreeUsers() {
        PagedResult<User> page = userRepository.findAll(0, 100);
        Map<String, Map<String, Map<String, List<User>>>> maps = new HashMap<>();
        page.content().forEach(user -> {
            maps.computeIfAbsent(user.country(), k -> new HashMap<>())
                    .computeIfAbsent(user.city(), k -> new HashMap<>())
                    .computeIfAbsent(user.state(), k -> new ArrayList<>())
                    .add(user);
        });
        return maps;
    }
}
