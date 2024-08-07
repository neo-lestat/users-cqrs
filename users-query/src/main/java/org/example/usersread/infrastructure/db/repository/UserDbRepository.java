package org.example.usersread.infrastructure.db.repository;

import org.example.usersread.application.repository.UserRepository;
import org.example.usersread.domain.model.User;
import org.example.usersread.infrastructure.db.entity.UserEntity;
import org.example.usersread.infrastructure.db.exception.UserNotFoundException;
import org.example.usersread.infrastructure.db.exception.UsernameNotAvailableException;
import org.example.usersread.infrastructure.db.mapper.UserEntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Page<User> findAll(Pageable pageable) {
        Page<UserEntity> pageUserEntity = userRepository.findAll(pageable);
        return new PageImpl<>(userMapper.toDomain(pageUserEntity.getContent()), pageable, pageUserEntity.getTotalElements());
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
        userRepository.findById(username)
                        .ifPresent(userRepository::delete);
    }

}
