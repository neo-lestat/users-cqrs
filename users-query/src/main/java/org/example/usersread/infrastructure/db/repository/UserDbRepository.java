package org.example.usersread.infrastructure.db.repository;

import org.example.usersread.application.repository.UserProjectionRepository;
import org.example.usersread.application.repository.UserReadRepository;
import org.example.usersread.domain.exception.UserNotFoundException;
import org.example.usersread.domain.exception.UsernameNotAvailableException;
import org.example.usersread.domain.model.PagedResult;
import org.example.usersread.domain.model.User;
import org.example.usersread.infrastructure.db.entity.UserEntity;
import org.example.usersread.infrastructure.db.mapper.UserEntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserDbRepository implements UserReadRepository, UserProjectionRepository {

    private final SpringDataUserRepository userRepository;
    private final UserEntityMapper userMapper;

    public UserDbRepository(SpringDataUserRepository userRepository, UserEntityMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public boolean usernameExist(String username) {
        return userRepository.findById(username).isPresent();
    }

    @Override
    public User findByUsername(String username) {
        UserEntity userEntity = userRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException("Username not found: " + username));
        return userMapper.toDomain(userEntity);
    }

    @Override
    public PagedResult<User> findAll(int pageNumber, int pageSize) {
        Page<UserEntity> pageUserEntity = userRepository.findAll(
                PageRequest.of(pageNumber, pageSize, Sort.by("username")));
        return new PagedResult<>(
                userMapper.toDomain(pageUserEntity.getContent()),
                pageUserEntity.getTotalPages(),
                pageUserEntity.getTotalElements());
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
    public User update(String username, User user) {
        UserEntity userEntityToUpdate = userRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException("Username not found: " + username));
        UserEntity userEntity = userMapper.toDbo(user);
        userEntityToUpdate.setEmail(userEntity.getEmail());
        userEntityToUpdate.setGender(userEntity.getGender());
        userEntityToUpdate.setName(userEntity.getName());
        userEntityToUpdate.setPictureUrl(userEntity.getPictureUrl());
        return userMapper.toDomain(userRepository.save(userEntityToUpdate));
    }

    @Override
    public void delete(String username) {
        userRepository.findById(username).ifPresent(userRepository::delete);
    }
}
