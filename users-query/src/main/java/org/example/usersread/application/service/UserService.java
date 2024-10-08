package org.example.usersread.application.service;

import org.example.usersread.application.repository.UserRepository;
import org.example.usersread.application.usecase.GetUserUseCase;
import org.example.usersread.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class UserService implements GetUserUseCase, UserCommand {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Page<User> getUsers(int pageNumber, int pageSize) {
        Pageable sortedByUsername = PageRequest.of(pageNumber, pageSize, Sort.by("username"));
        return userRepository.findAll(sortedByUsername);
    }

    public Map<String, Map<String, Map<String, List<User>>>> getTreeUsers(/*int pageNumber, int pageSize*/) {
        Pageable sortedByUsername = PageRequest.of(0, 100, Sort.by("username"));
        Page<User> page = userRepository.findAll(sortedByUsername);
        Map<String, Map<String, Map<String, List<User>>>> maps = new HashMap<>();
        page.getContent().forEach(user -> {
            maps.computeIfAbsent(user.country(), k -> new HashMap<>())
                    .computeIfAbsent(user.city(), k -> new HashMap<>())
                    .computeIfAbsent(user.state(), k -> new ArrayList<>())
                    .add(user);
        });
        return maps;
    }

    public void delete(String username) {
        userRepository.delete(username);
    }

    public void saveOrUpdate(User user) {
        if (userRepository.usernameExist(user.username())) {
            userRepository.update(user.username(), user);
        } else {
            userRepository.save(user);
        }
    }
}
