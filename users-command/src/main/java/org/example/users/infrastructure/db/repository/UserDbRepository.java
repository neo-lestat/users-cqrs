package org.example.users.infrastructure.db.repository;

import org.example.users.application.repository.UserRepository;
import org.example.users.domain.model.User;
import org.example.users.infrastructure.db.entity.UserEntity;
import org.example.users.infrastructure.db.exception.UserNotFoundException;
import org.example.users.infrastructure.db.exception.UsernameNotAvailableException;
import org.example.users.infrastructure.db.mapper.UserEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserDbRepository implements UserRepository {

    private final SpringDataUserRepository userRepository;

    private final UserEntityMapper userMapper;

    public UserDbRepository(SpringDataUserRepository userRepository,
                            UserEntityMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        Optional<UserEntity> userEntity = userRepository.findById(user.username());
        userEntity.ifPresent(e -> {
            throw new UsernameNotAvailableException("Username already exists: " + user.username());
        });
        return userMapper.toDomain(userRepository.save(userMapper.toDbo(user)));
    }

    @Override
    public List<User> save(List<User> users) {
        List<User> usersToSave = new ArrayList<>();
        Set<String> nameSet = new HashSet<>();
        //remove duplicates by username from users generated
        //then filter users that already exist in db
        users.stream()
             .filter(u -> nameSet.add(u.username()))
             .forEach(user -> {
                 Optional<UserEntity> userEntity = userRepository.findById(user.username());
                 if (userEntity.isEmpty()) {
                     usersToSave.add(user);
                 }
             });
        return userMapper.toDomain(userRepository.saveAll(userMapper.toDbo(usersToSave)));
    }

    @Override
    public User update(String username, User user) {
        UserEntity userEntityToUpdate = getUserEntity(username);
        UserEntity userEntity = userMapper.toDbo(user);
        userEntityToUpdate.setEmail(userEntity.getEmail());
        userEntityToUpdate.setGender(userEntity.getGender());
        userEntityToUpdate.setName(userEntity.getName());
        userEntityToUpdate.setPictureUrl(userEntity.getPictureUrl());
        return userMapper.toDomain(userRepository.save(userEntityToUpdate));
    }

    private UserEntity getUserEntity(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Not found username: %s", username)));
    }

    @Override
    public void delete(String username) {
        userRepository.delete(getUserEntity(username));
    }

}
